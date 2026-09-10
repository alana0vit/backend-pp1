package br.com.conectaPro.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.conectaPro.api.user.AddressUserRequest;
import br.com.conectaPro.api.user.UserRequest;
import br.com.conectaPro.dto.CoordinatesDTO;
import br.com.conectaPro.model.category.CategoryRepository;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.AddressUserRepository;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserRepository;
import br.com.conectaPro.model.user.UserService;
import br.com.conectaPro.model.user.UserType;
import br.com.conectaPro.util.GeoLocationService;
import br.com.conectaPro.util.Util;

@ExtendWith(MockitoExtension.class)//Meio que deixa todos os mock ligados para teste HH
 class UserServiceTest {

    //todo o processo para testar o UserService, precisa de todas as dependencias dele HH
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressUserRepository addressUserRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private GeoLocationService geoLocationService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
  
    @Test
    @DisplayName("deve buscar um usuario por email")
    void deveBuscarUsuarioPorEmail(){
        String email = "cleiton@gmail.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        
        User resultado = userService.findByEmail(email);
        
        assertEquals(user, resultado);
    }

    @Test 
    @DisplayName("deve lancar uma excecao caso um usuario nao seja encontrado por email")
    void UsuarioNaoEncontrado(){
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
        userService.findByEmail(email);
    });
    }

    @Test
    void deveBuscarUsuarioPorId(){
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(user));

        User resultado = userService.getById(id);
        assertEquals(user, resultado);
    }

    @Test
    @DisplayName("deve lancar uma excecao caso um usuario nao seja encontrado por id")
    void UsuarioNaoEncontradoPorId(){
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getById(id);
        });
    }

    @Test
    @DisplayName("deve deletar um usuario por id")
    void DeletarUsuarioPorId(){
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setEnabled(true);

        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(user));

        userService.delete(id);

        assertEquals(false, user.getEnabled());
    }

    @Test 
    @DisplayName("deve lancar uma excecao caso um usuario nao seja encontrado para deletar")
    void DeletarUsuarioNaoEncontradoPorId(){
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.delete(id);
        });
    }

    @Test
    @DisplayName("Valida que um usuario/cliente nao consiga ter categorias profissionais")
    void validarProfissionalCategoria(){
        UserRequest userRequest = UserRequest.builder()
                .userType(UserType.CLIENT)
                .categoriesIds(java.util.Arrays.asList(1L, 2L))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        userService.save(userRequest); 
        });

        assertEquals("Erro de negócio: Clientes não podem possuir categorias profissionais vinculadas.", exception.getMessage());
    }

    @Test
    @DisplayName("deve criar um usuario com sucesso")
    void deveCriarUsuario(){

        //Preparacao dos dados usados no teste HH
         AddressUserRequest address = AddressUserRequest.builder()
                .street("Rua Teste")
                .number("123")
                .neighborhood("Centro")
                .city("Recife")
                .state("PE")
                .zipCode("50000-000")
                .supplement("Casa")
                .build();
        UserRequest userRequest = UserRequest.builder()
                .name("Cleiton")
                .email("cleiton@gmail.com")
                .password("123456")
                .birthDate(LocalDate.of(2000, 1, 1))
                .phone("81999999999")
                .userType(UserType.CLIENT)
                .registryId("123456789")
                .address(address)
                .build();

        //Add os Mocks para simular o comportamento das dependencias do UserService HH

        //Simulando a cricao e criptografia da senha do usuario HH
        when(passwordEncoder.encode("123456"))
        .thenReturn("senha-criptografada");

        //Simulando o salvamento do usuario no banco de dados e faz o repositorio retornar o usuario salvo HH
        when(userRepository.save(any(User.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

        //Um objeto de coordenadas para simular a resposta do geo HH
        CoordinatesDTO coordinates = new CoordinatesDTO();
        coordinates.setLat("-8.0476");
        coordinates.setLon("-34.8770");

        //Simulando a chamada ao serviço de geolocalização para obter as coordenadas do endereço do usuário HH
        when(geoLocationService.getCoordinates(any(AddressUser.class)))
        .thenReturn(coordinates);   

        //Aqui que a brincadeira acontece o usuario é criado e salvo no banco de dados, e o resultado é retornado para ser testado HH
        User resultado = userService.save(userRequest);

        //Vamos verificar se o resultado do teste e o esperado HH
        assertEquals("Cleiton", resultado.getName());
        assertEquals("cleiton@gmail.com", resultado.getEmail());
        assertEquals("senha-criptografada", resultado.getPassword());
        assertEquals(UserType.CLIENT, resultado.getUserType());
        assertEquals(true, resultado.getEnabled());
        assertEquals(1, resultado.getAdresses().size());
    }


    @Test
    @DisplayName("Deve atualizar um usuario existente")
    void deveAtualizarUsuario(){
        Long id = 1L;

        User usuarioExist = new User();
        usuarioExist.setId(id);
        usuarioExist.setName("Cleiton");
        usuarioExist.setEmail("antigo@gmail.com");
        usuarioExist.setPassword("senha-antiga");
        usuarioExist.setUserType(UserType.CLIENT);

        UserRequest userRequest = UserRequest.builder()
                .name("Cleiton Novo")
                .email("novo@gmail.com")
                .password("novaSenha123")
                .userType(UserType.CLIENT)
                .build();

        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(usuarioExist));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senha-criptografada");

        userService.update(id, userRequest);

        assertEquals("Cleiton Novo", usuarioExist.getName());
        assertEquals("novo@gmail.com", usuarioExist.getEmail());
        assertEquals("senha-criptografada", usuarioExist.getPassword());

    }

    @Test
    @DisplayName("Deve atualizar a foto de um usuario existente")
    void UpdateFotoUsuario(){

        Long id = 1L;

        User usuarioExist = new User();
        usuarioExist.setId(id);
        usuarioExist.setPhoto("foto-antiga.jpg");

        //O mock vai simular o envio de uma nova foto para o usuário, mas como não estamos testando o upload real, 
        // podemos usar um MockMultipartFile vazio para representar a nova foto. HH
        MockMultipartFile fotoNova = new MockMultipartFile("foto", "foto-nova.jpg", "image/jpeg", new byte[0]);

        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(usuarioExist));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mockando o método estático Util.fazerUploadImagem para retornar um nome de arquivo simulado
        try (MockedStatic<Util> utilMock = mockStatic(Util.class)) {
        utilMock.when(() -> Util.fazerUploadImagem(fotoNova)).thenReturn("nova_foto_123.jpg");

        User resultado = userService.updatePhoto(id, fotoNova);

        assertEquals("nova_foto_123.jpg", resultado.getPhoto()); 
        }
    }

    @Test
    @DisplayName("Deve buscar usuarios com base nos filtros fornecidos")
    void BuscandoUsuario(){
        String name = "Jose";
        Long categoriaId = 1l;
        Double latitude = -8.0476;
        Double longitude = -34.8770;
        Double radiusKm = 10.0;

        User user = new User();
        user.setId(1L);
        user.setName(name);
        user.setUserType(UserType.PROFESSIONAL);

        when(userRepository.searchUsers(name, categoriaId, latitude, longitude, radiusKm))
                .thenReturn(java.util.Arrays.asList(user));
        
        List<User> resultado = userService.search(name, categoriaId, latitude, longitude, radiusKm);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuários com enderecos invalidos")
    void adicionarEnderecoUsuario(){
        Long id = 1L;

        User usuarioExist = new User();
        usuarioExist.setId(id);
        usuarioExist.setAdresses(new ArrayList<>());

        AddressUser address = new AddressUser();
        address.setStreet("Rua Teste");
        address.setNumber("123");
        address.setNeighborhood("Centro");
        address.setCity("Recife");
        address.setState("PE");
        address.setZipCode("50000-000");

        CoordinatesDTO coordinates = new CoordinatesDTO();
        coordinates.setLat("-8.0476");
        coordinates.setLon("-34.8770");

        when(userRepository.findById(id)).thenReturn(java.util.Optional.of(usuarioExist));
        when(geoLocationService.getCoordinates(address)).thenReturn(coordinates);

        AddressUser resultado = userService.postAddressUser(id, address);

        assertEquals(address, resultado);
        assertEquals(usuarioExist, resultado.getUserId());
        assertEquals(true, resultado.getEnabled());
        assertEquals(-8.0476, resultado.getLatitude());
        assertEquals(-34.8770, resultado.getLongitude());
        assertEquals(1, usuarioExist.getAdresses().size());
    }

    @Test
    @DisplayName("Levando excecao caso nao encontrar um endereco de usuario por id")
    void EnderecoNaoEncontradoPorId(){
        Long id = 1L;
        when(addressUserRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getAddressById(id);
        });
    }

    @Test
    @DisplayName("deve deletar um endereco de usuario existente")
    void DeletarEndrecoUsuario() {

        Long id = 1L;

        User user = new User();
        user.setId(1L);

        AddressUser address = new AddressUser();
        address.setId(id);
        address.setEnabled(true);
        address.setUserId(user);

        user.setAdresses(new ArrayList<>());
        user.getAdresses().add(address);

        when(addressUserRepository.findById(id))
                .thenReturn(java.util.Optional.of(address));

        when(userRepository.findById(1L))
                .thenReturn(java.util.Optional.of(user));

        userService.deleteAddressUser(id);

        assertEquals(false, address.getEnabled());
}

    @Test
    @DisplayName("Deve atualizar um endereco de usuario existente")
    void AtualizarEnderecoUsuario(){
        
        Long id = 1L;
        User user = new User();
        user.setId(1L);

        AddressUser address = new AddressUser();
        address.setId(id);
        address.setStreet("Rua Teste");
        address.setNumber("123");
        address.setNeighborhood("Centro");
        address.setCity("Recife");
        address.setState("PE");
        address.setZipCode("50000-000");

        AddressUser updatedAddress = new AddressUser();
        updatedAddress.setStreet("Rua Atualizada");
        updatedAddress.setNumber("456");
        updatedAddress.setNeighborhood("Centro");
        updatedAddress.setCity("Recife");
        updatedAddress.setState("PE");
        updatedAddress.setZipCode("50000-000");

        CoordinatesDTO coordinates = new CoordinatesDTO();
        coordinates.setLat("-8.0476");
        coordinates.setLon("-34.8770");

        when(addressUserRepository.findById(id)).thenReturn(java.util.Optional.of(address));
        when(geoLocationService.getCoordinates(any(AddressUser.class))).thenReturn(coordinates);
        when(addressUserRepository.save(any(AddressUser.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

        AddressUser resultado = userService.updateAddressUser(id, updatedAddress);

        assertEquals("Rua Atualizada", resultado.getStreet());
        assertEquals("456", resultado.getNumber());
        assertEquals("Centro", resultado.getNeighborhood());
        assertEquals("Recife", resultado.getCity());
        assertEquals("PE", resultado.getState());
        assertEquals("50000-000", resultado.getZipCode());

    }

    @Test
    @DisplayName("Deve lancar uma excecao caso um endereco nao seja encontrado para tentar atualizar")
    void AtualizarEnderecoNaoEncontradoUsuario() {
        Long id = 1L;
        AddressUser updatedAddress = new AddressUser();
        updatedAddress.setStreet("Rua Atualizada");

        when(addressUserRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // aqui estamos pegando a exceção que é lançada quando o endereço não é encontrado e verificando se a mensagem da exceção é a esperada HH
        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            userService.updateAddressUser(id, updatedAddress);
        });
        assertEquals("Endereço não encontrado com o ID: 1", excecao.getMessage());
    }
}
