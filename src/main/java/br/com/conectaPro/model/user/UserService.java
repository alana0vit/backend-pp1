package br.com.conectaPro.model.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.conectaPro.api.user.UserRequest;
import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.category.CategoryRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressUserRepository addressUserRepository;
    private final CategoryRepository categoryRepository;

    public UserService(UserRepository repository, AddressUserRepository addressUserRepository,
            CategoryRepository categoryRepository) {
        this.userRepository = repository;
        this.addressUserRepository = addressUserRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public User save(UserRequest userRequest) {
        validateProfessionalCategories(userRequest.getUserType(), userRequest.getCategoriesIds());

        // Instancia e salva o User
        User user = userRequest.build();
        if (userRequest.getCategoriesIds() != null && !userRequest.getCategoriesIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(userRequest.getCategoriesIds());
            user.setCategories(categories);
        }

        user.setEnabled(Boolean.TRUE);
        User savedUser = userRepository.save(user);

        // Instancia e salva o Address vinculado ao User
        AddressUser addressUser = userRequest.getAddress().build();
        addressUser.setUserId(savedUser);
        addressUser.setEnabled(Boolean.TRUE);
        addressUserRepository.save(addressUser);

        // Atualiza a lista no objeto (para retornos imediatos, se necessário)
        List<AddressUser> addresses = new ArrayList<>();
        addresses.add(addressUser);
        savedUser.setAdresses(addresses);

        return savedUser;
    }

    private void validateProfessionalCategories(UserType userType, List<Long> categoryIds) {
        if (UserType.CLIENT.equals(userType) && categoryIds != null && !categoryIds.isEmpty()) {
            // Vou jogar uma exceção aqui, talvez precise reformuçar a maneira de lidar com
            // erros
            // Pode ser pega pelo controller e soltar um 400 (Bad Request)
            throw new IllegalArgumentException(
                    "Erro de negócio: Clientes não podem possuir categorias profissionais vinculadas.");
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o email: " + email));
    }

    public List<User> getAll() {

        return userRepository.findAll();
    }

    public User getById(Long id) {

        return userRepository.findById(id).get();
    }

    @Transactional
    public void update(Long id, User userChanged, List<Long> categoryIds) {

        User user = userRepository.findById(id).get(); // Talvez add um orElseThrow()

        validateProfessionalCategories(userChanged.getUserType(), categoryIds);

        user.setName(userChanged.getName());
        user.setEmail(userChanged.getEmail());
        user.setBirthDate(userChanged.getBirthDate());
        user.setPhone(userChanged.getPhone());
        user.setUserType(userChanged.getUserType());
        user.setRegistryId(userChanged.getRegistryId());

        if (userChanged.getPassword() != null && userChanged.getPassword().isBlank()) {
            user.setPassword(userChanged.getPassword());
        }

        if (userChanged.getAdresses() != null) {
            user.getAdresses().clear();
            // Garante a integridade da Foreign Key (Essencial!)
            userChanged.getAdresses().forEach(address -> address.setUserId(user));
            user.getAdresses().addAll(userChanged.getAdresses());
        }

        if (categoryIds != null) {
            List<Category> categories = categoryRepository.findAllById(categoryIds);
            user.setCategories(categories);
        } else {
            user.setCategories(new ArrayList<>());
        }

        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {

        User user = userRepository.findById(id).get();
        user.setEnabled(Boolean.FALSE);

        userRepository.save(user);
    }

    // Filtros de busca para user do tipo "PROFESSIONAL"

    public List<User> search(String name, Long categoryId,
            Double lat, Double lng, Double radiusKm) {

        if (radiusKm != null && radiusKm > 20) {
            throw new IllegalArgumentException("Distância máxima permitida alcançada");
        }

        return userRepository.searchUsers(name, categoryId, lat, lng, radiusKm);
    }

    // Endereços

    public List<AddressUser> getAllAddressByUser(Long userId) {
        User user = this.getById(userId);
        return user.getAdresses();
    }

    public AddressUser getAddressById(Long id) {
        return addressUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    @Transactional
    public AddressUser postAddressUser(Long userId, AddressUser address) {

        User user = this.getById(userId);

        address.setUserId(user);
        address.setEnabled(Boolean.TRUE);
        addressUserRepository.save(address);

        List<AddressUser> listAddressUser = user.getAdresses();

        if (listAddressUser == null) {
            listAddressUser = new ArrayList<>();
        }

        listAddressUser.add(address);
        user.setAdresses(listAddressUser);
        userRepository.save(user);

        return address;
    }

    @Transactional
    public AddressUser updateAddressUser(Long id, AddressUser addressChanged) {

        AddressUser address = addressUserRepository.findById(id).get();
        address.setStreet(addressChanged.getStreet());
        address.setNumber(addressChanged.getNumber());
        address.setNeighborhood(addressChanged.getNeighborhood());
        address.setCity(addressChanged.getCity());
        address.setState(addressChanged.getState());
        address.setZipCode(addressChanged.getZipCode());
        address.setLatitude(addressChanged.getLatitude());
        address.setLongitude(addressChanged.getLongitude());
        address.setSupplement(addressChanged.getSupplement());

        return addressUserRepository.save(address);
    }

    @Transactional
    public void deleteAddressUser(Long idAddress) {

        AddressUser address = addressUserRepository.findById(idAddress).get();
        address.setEnabled(Boolean.FALSE);
        addressUserRepository.save(address);

        User user = this.getById(address.getUserId().getId());
        user.getAdresses().remove(address);
        userRepository.save(user);
    }

}
