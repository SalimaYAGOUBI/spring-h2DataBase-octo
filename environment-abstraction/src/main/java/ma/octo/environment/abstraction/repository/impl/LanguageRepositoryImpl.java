package ma.octo.environment.abstraction.repository.impl;

import ma.octo.environment.abstraction.database.H2DataBaseConfig;
import ma.octo.environment.abstraction.entity.Language;
import ma.octo.environment.abstraction.repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class LanguageRepositoryImpl implements LanguageRepository {

  @Autowired
  H2DataBaseConfig h2DataBaseConfig;

  @Override
  public Optional<Language> findByExtension(final String extension) {
    return h2DataBaseConfig.getLanguageByExtension(extension);
  }

  @Override
  public Optional<Language> findById(final String id) {
    return h2DataBaseConfig.getLanguageById(id);
  }

  @Override
  public List<Language> findAll() {
    return h2DataBaseConfig.getAllLanguages();
  }

  private Predicate<Language> languageExtensionPredicate(final String extension) {
    return language -> Objects.equals(extension, language.getFileExtension());
  }

  private Predicate<Language> languageIdPredicate(final String id) {
    return language -> Objects.equals(id, language.getId());
  }
}
