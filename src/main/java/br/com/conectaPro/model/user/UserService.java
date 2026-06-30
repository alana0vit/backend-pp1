package br.com.conectaPro.model.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.conectaPro.api.user.UserRequest;
import br.com.conectaPro.dto.CoordinatesDTO;
import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.category.CategoryRepository;
import br.com.conectaPro.util.GeoLocationService;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressUserRepository addressUserRepository;
    private final CategoryRepository categoryRepository;
    private final GeoLocationService geoLocationService;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository repository,
            AddressUserRepository addressUserRepository,
            CategoryRepository categoryRepository,
            GeoLocationService geoLocationService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = repository;
        this.addressUserRepository = addressUserRepository;
        this.categoryRepository = categoryRepository;
        this.geoLocationService = geoLocationService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User save(UserRequest userRequest) {
        validateProfessionalCategories(userRequest.getUserType(), userRequest.getCategoriesIds());

        User user = userRequest.build();

        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        if (userRequest.getCategoriesIds() != null && !userRequest.getCategoriesIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(userRequest.getCategoriesIds());
            user.setCategories(categories);
        }

        user.setEnabled(Boolean.TRUE);
        User savedUser = userRepository.save(user);

        // Instancia e salva o Address vinculado ao User
        AddressUser addressUser = userRequest.getAddress().build();
        try {
            CoordinatesDTO coords = geoLocationService.getCoordinates(addressUser);
            addressUser.setLatitude(Double.parseDouble(coords.getLat()));
            addressUser.setLongitude(Double.parseDouble(coords.getLon()));
            System.out.println("LAT: " + addressUser.getLatitude());
            System.out.println("LNG: " + addressUser.getLongitude());
        } catch (Exception e) {
            System.out.println("ERRO GEOLOCALIZACAO: " + e.getMessage());
        }

        addressUser.setUserId(savedUser);
        addressUser.setEnabled(Boolean.TRUE);
        addressUserRepository.save(addressUser);

        List<AddressUser> addresses = new ArrayList<>();
        addresses.add(addressUser);
        savedUser.setAdresses(addresses);

        return savedUser;
    }

    private void validateProfessionalCategories(UserType userType, List<Long> categoryIds) {
        if (UserType.CLIENT.equals(userType) && categoryIds != null && !categoryIds.isEmpty()) {
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
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    @Transactional
    public void update(Long id, UserRequest userRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));

        validateProfessionalCategories(userRequest.getUserType(), userRequest.getCategoriesIds());

        user.setName(userRequest.getName());
        user.setEnterprise(userRequest.getEnterprise());
        user.setEmail(userRequest.getEmail());
        user.setBirthDate(userRequest.getBirthDate());
        user.setPhone(userRequest.getPhone());
        user.setUserType(userRequest.getUserType());
        user.setRegistryId(userRequest.getRegistryId());
        user.setPhoto(userRequest.getPhoto());

        if (userRequest.getPassword() != null && !userRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        if (userRequest.getCategoriesIds() != null) {
            List<Category> categories = categoryRepository.findAllById(userRequest.getCategoriesIds());
            user.setCategories(categories);
        } else {
            user.setCategories(new ArrayList<>());
        }

        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
        user.setEnabled(Boolean.FALSE);
        userRepository.save(user);
    }

    public List<User> search(
            String name,
            Long categoryId,
            Double latitude,
            Double longitude,
            Double radiusKm) {

        if (radiusKm != null && radiusKm <= 0) {
            throw new IllegalArgumentException("O raio deve ser maior que zero");
        }

        return userRepository.searchUsers(name, categoryId, latitude, longitude, radiusKm);
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

        try {
            CoordinatesDTO coords = geoLocationService.getCoordinates(address);
            address.setLatitude(Double.parseDouble(coords.getLat()));
            address.setLongitude(Double.parseDouble(coords.getLon()));
        } catch (Exception e) {
            System.out.println("ERRO GEOLOCALIZACAO: " + e.getMessage());
        }

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
        AddressUser address = addressUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com o ID: " + id));

        address.setStreet(addressChanged.getStreet());
        address.setNumber(addressChanged.getNumber());
        address.setNeighborhood(addressChanged.getNeighborhood());
        address.setCity(addressChanged.getCity());
        address.setState(addressChanged.getState());
        address.setZipCode(addressChanged.getZipCode());
        address.setSupplement(addressChanged.getSupplement());

        CoordinatesDTO coords = geoLocationService.getCoordinates(address);
        address.setLatitude(Double.valueOf(coords.getLat()));
        address.setLongitude(Double.valueOf(coords.getLon()));

        return addressUserRepository.save(address);
    }

    @Transactional
    public void deleteAddressUser(Long idAddress) {
        AddressUser address = addressUserRepository.findById(idAddress)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado com o ID: " + idAddress));

        address.setEnabled(Boolean.FALSE);
        addressUserRepository.save(address);

        User user = this.getById(address.getUserId().getId());
        user.getAdresses().remove(address);
        userRepository.save(user);
    }
}