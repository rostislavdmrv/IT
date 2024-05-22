package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.json.CountrySeedDto;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;
import softuni.exam.util.ValidationUtil;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {
    private static final String COUNTRIES_FILE_PATH = "src/main/resources/files/json/countries.json";
    private final CountryRepository countryRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public CountryServiceImpl(CountryRepository countryRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.countryRepository = countryRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }


    @Override
    public boolean areImported() {
        return countryRepository.count() > 0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files
                .readString(Path.of(COUNTRIES_FILE_PATH));
    }

    @Override
    public String importCountries() throws IOException {
//        StringBuilder sb = new StringBuilder();
//
//        CountrySeedDto[] countrySeedDtos = gson.fromJson(new FileReader(COUNTRIES_FILE_PATH), CountrySeedDto[].class);
//        // Обхождане на всички държави и запазване в базата данни
//        for (CountrySeedDto countrySeedDto : countrySeedDtos) {
//            Optional<Country> optionalCountry = countryRepository.findByName(countrySeedDto.getName());
//
//            // Проверка за валидност и дублиране на държавата
//            if (!validationUtil.isValid(countrySeedDto) || optionalCountry.isPresent()) {
//                sb.append("Invalid country: ").append(countrySeedDto.getName()).append(System.lineSeparator());
//                continue;
//            }
//
//            // Създаване на обект Country и запазване в базата данни
//            Country country = modelMapper.map(countrySeedDto, Country.class);
//            countryRepository.saveAndFlush(country);
//            sb.append(String.format("Successfully imported country %s - %s", country.getName(), country.getCapital())).append(System.lineSeparator());
//        }
//
//        return sb.toString();
        StringBuilder sb = new StringBuilder();

        Arrays.stream(gson
                        .fromJson(readCountriesFromFile(), CountrySeedDto[].class))
                .filter(countrySeedDto -> {
                    boolean isValid = validationUtil.isValid(countrySeedDto);

                    Optional<Country> countryByName = countryRepository.findByName(countrySeedDto.getName());
                    if (countryByName.isPresent()){
                        isValid = false;
                    }

                    sb.append(isValid
                                    ? String.format("Successfully imported country %s - %s", countrySeedDto.getName(), countrySeedDto.getCapital())
                                    : "Invalid country")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(countrySeedDto -> modelMapper.map(countrySeedDto, Country.class))
                .forEach(countryRepository::save);

        return sb.toString().trim();

    }
    @Override
    public Optional<Country> getCountryById(Long countryId) {
        return countryRepository.findById(countryId);
    }

    @Override
    public void saveAddedVolcanoInCountry(Country country) {
        countryRepository.save(country);
    }
}
