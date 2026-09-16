package br.com.conectaPro.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import br.com.conectaPro.model.category.CategoryRepository;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.AddressUserRepository;
import br.com.conectaPro.model.user.UserRepository;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.model.user.UserType;
import br.com.conectaPro.util.GeoLocationService;
import br.com.conectaPro.dto.CoordinatesDTO;
import br.com.conectaPro.api.user.AddressUserRequest;
import br.com.conectaPro.api.user.UserRequest;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.util.Util;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UserServiceIT {
    
    @Autowired 
    UserRepository userRepository;
    @Autowired 
    AddressUserRepository addressUserRepository;
    @Autowired 
    CategoryRepository categoryRepository;
    @MockitoBean
    GeoLocationService geoLocationService;
    @Autowired 
    PasswordEncoder passwordEncoder;
    @Autowired 
    UserService userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        addressUserRepository.deleteAll();
        categoryRepository.deleteAll();

       CoordinatesDTO mockCoords = new CoordinatesDTO();
        mockCoords.setLat("-8.04756");
        mockCoords.setLon("-34.87700");
        when(geoLocationService.getCoordinates(any())).thenReturn(mockCoords);
    }

    @Test
    @DisplayName("Deve salvar usuario profissional")
    void SalvarUsuarioProfissional(){
        Category categoria = new Category();
        categoria.setName("Categoria teste");
        categoria.setDescription("Descricao teste");
        Category categoriaSalva = categoryRepository.save(categoria);

        AddressUserRequest address = new AddressUserRequest();
        address.setStreet("Rua teste");
        address.setNumber("123");
        address.setCity("Recife");
        address.setState("PE");

        UserRequest profissional = new UserRequest();
        profissional.setName("Profissional teste");
        profissional.setEmail("teste@gmail.com");
        profissional.setPassword("senha123");
        profissional.setUserType(UserType.PROFESSIONAL);
        profissional.setCategoriesIds(List.of(categoriaSalva.getId()));
        profissional.setAddress(address);

        User usuarioSalvo = userService.save(profissional);

        assertEquals("Profissional teste", usuarioSalvo.getName());
        assertTrue(usuarioSalvo.getEnabled());
        assertTrue(passwordEncoder.matches("senha123", usuarioSalvo.getPassword()));
        assertEquals(1, usuarioSalvo.getAdresses().size());
        assertEquals(-8.04756, usuarioSalvo.getAdresses().get(0).getLatitude());
    } 
    
    @Test 
    @DisplayName("Deve salvar usuario Cliente")
    void SalvaUsuarioCliente(){
        AddressUserRequest address = new AddressUserRequest();
        address.setStreet("Rua teste");
        address.setNumber("123");
        address.setCity("Recife");
        address.setState("PE");

        UserRequest cliente = new UserRequest();
        cliente.setName("Cliente teste");
        cliente.setEmail("teste@gmail.com");
        cliente.setPassword("senha123");
        cliente.setUserType(UserType.CLIENT);
        cliente.setAddress(address);

        User usuarioSalvo = userService.save(cliente);

        assertEquals(UserType.CLIENT, usuarioSalvo.getUserType());
    }

    @Test
    @DisplayName("Deve fazer update no usuario")
    void UpdateUsuario(){
        User usuario = User.builder()
            .name("Usuario")
            .email("usuario@gmail.com")
            .password("senha123")
            .userType(UserType.CLIENT)
            .build();
        usuario.setEnabled(Boolean.TRUE);
        User usuarioSalvo = userRepository.save(usuario);

        UserRequest clienteUpdate = new UserRequest();
        clienteUpdate.setName("usuarioUpdate");
        clienteUpdate.setEmail("update@gmail.com");
        clienteUpdate.setPassword("senha123");
        clienteUpdate.setUserType(UserType.CLIENT);

        userService.update(usuarioSalvo.getId(), clienteUpdate);
        User verificar = userRepository.findById(usuarioSalvo.getId()).orElseThrow();

        assertEquals("usuarioUpdate", verificar.getName());
        assertEquals("update@gmail.com", verificar.getEmail());
    }

    @Test 
    @DisplayName("Deve levantar excecao caso client estiver com categoria")
    void ExcecaoClienteCategoria(){
        Category categoria = new Category();
        categoria.setName("Categoria teste");
        categoria.setDescription("Descricao teste");
        Category categoriaSalva = categoryRepository.save(categoria);

        UserRequest cliente = new UserRequest();
        cliente.setName("Cliente teste");
        cliente.setEmail("teste@gmail.com");
        cliente.setPassword("senha123");
        cliente.setUserType(UserType.CLIENT);
        cliente.setCategoriesIds(List.of(categoriaSalva.getId()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->{
            userService.save(cliente);
        });

        assertEquals("Erro de negócio: Clientes não podem possuir categorias profissionais vinculadas.", exception.getMessage());
    }

    @Test 
    @DisplayName("Deve fazer update na foto do usuario")
    void UpdateFoto(){
        User usuario = User.builder()
            .name("Usuario Foto")
            .email("foto@gmail.com")
            .password("senha123")
            .userType(UserType.CLIENT)
            .build();
        usuario.setEnabled(Boolean.TRUE);
        User usuarioSalvo = userRepository.save(usuario);

        MockMultipartFile foto = new MockMultipartFile(
                "foto",
                "perfil.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        try (var mockedUtil = org.mockito.Mockito.mockStatic(Util.class)) {
            mockedUtil.when(() -> Util.fazerUploadImagem(foto)).thenReturn("perfil_renomeado.jpg");

            User usuarioAtualizado = userService.updatePhoto(usuarioSalvo.getId(), foto);
            
            User verificando = userRepository.findById(usuarioSalvo.getId()).orElseThrow();
            assertEquals("perfil_renomeado.jpg", usuarioAtualizado.getPhoto());
            assertEquals(usuarioAtualizado.getPhoto(), verificando.getPhoto());
        }
    }

    @Test 
    @DisplayName("Deve levantar excecao quando o upload da foto falhar")
    void deveLancarExcecaoQuandoUploadFalhar() {
        User usuario = User.builder()
            .name("Usuario Foto")
            .email("foto@gmail.com")
            .password("senha123")
            .userType(UserType.CLIENT)
            .build();
        usuario.setEnabled(Boolean.TRUE);
        User usuarioSalvo = userRepository.save(usuario);

        MockMultipartFile foto = new MockMultipartFile(
                "foto",
                "perfil.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updatePhoto(usuarioSalvo.getId(), foto);
        });

        assertEquals("Erro ao salvar a foto: perfil.jpg", exception.getMessage());
    }

    @Test 
    @DisplayName("Deve deletar a foto do usuario")
    void DeletarFotoDoUsuario(){
        User usuario = User.builder()
            .name("Usuario Foto")
            .email("foto@gmail.com")
            .password("senha123")
            .userType(UserType.CLIENT)
            .phone("foto_delete.jpg")
            .build();
        usuario.setEnabled(Boolean.TRUE);
        User usuarioSalvo = userRepository.save(usuario);

        userService.deletePhoto(usuarioSalvo.getId());
        User verificar = userRepository.findById(usuarioSalvo.getId()).orElseThrow();

        assertNull(verificar.getPhoto());
    }

    @Test
    @DisplayName("Deve adicionar um novo endereco com usuario")
    void AdicionarEndereco(){
        User usuario = User.builder()
            .name("Usuario Foto")
            .email("foto@gmail.com")
            .password("senha123")
            .userType(UserType.CLIENT)
            .phone("foto_delete.jpg")
            .build();
        usuario.setEnabled(Boolean.TRUE);
        User usuarioSalvo = userRepository.save(usuario);

        AddressUser address = new AddressUser();
        address.setStreet("Rua teste");
        address.setNumber("123");
        address.setCity("Recife");
        address.setState("PE");

        userService.postAddressUser(usuarioSalvo.getId(), address);

        User verificar = userRepository.findById(usuarioSalvo.getId()).orElseThrow();
        assertEquals(1, verificar.getAdresses().size());
        assertEquals("Rua teste", verificar.getAdresses().get(0).getStreet());
    }

    @Test
    @DisplayName("Deve fazer update em um endereco")
    void UpdateEndereco(){
        AddressUser address = AddressUser.builder()
            .street("Rua teste")
            .number("123")
            .city("Recife")
            .state("PE")
            .build();
        AddressUser enderecoSalvo = addressUserRepository.save(address);

        AddressUser novoEndereco = AddressUser.builder()
                .street("Rua teste update")
                .number("123")
                .city("RecifeUpdate")
                .state("PE")
                .build();
        AddressUser enderecoUpdate = userService.updateAddressUser(enderecoSalvo.getId(),novoEndereco);

        assertEquals("Rua teste update", enderecoUpdate.getStreet());
        assertEquals("RecifeUpdate", enderecoUpdate.getCity());
    }

    @Test
    @DisplayName("Deve Buscar profissional perto")
    public void DeveUsarFiltro() {
        Category categoria = new Category();
        categoria.setName("Categoria teste");
        categoria.setDescription("Descricao teste");
        Category categoriaSalva = categoryRepository.save(categoria);

        User profissional = User.builder()
                .name("profissional")
                .email("foto@gmail.com")
                .password("senha123")
                .userType(UserType.PROFESSIONAL)
                .categories(new ArrayList<>(List.of(categoriaSalva)))
                .adresses(new ArrayList<>())
                .build();
        profissional.setEnabled(Boolean.TRUE);
        User usuarioSalvo = userRepository.save(profissional);

        AddressUser endereco = AddressUser.builder()
                .street("Rua teste")
                .number("123")
                .city("Recife")
                .state("PE")
                .latitude(-8.04756)
                .longitude(-34.87700)
                .build();
        endereco.setUserId(usuarioSalvo);
        addressUserRepository.save(endereco);
        List<User> resultado = userService.search("profissional", categoriaSalva.getId(), -8.04756, -34.87700, 10.0);

        assertEquals("profissional", resultado.get(0).getName());
    }

    @Test
    @DisplayName("Deve levantar excecao quando o raio for maior que zero")
    void DeveLancarMairoZero(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            userService.search("profissional", 1L, -8.04756, -34.87700, 0.0)
        );

        assertEquals("O raio deve ser maior que zero", exception.getMessage());
    }

}
