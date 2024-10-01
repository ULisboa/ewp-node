package pt.ulisboa.ewp.node;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import pt.ulisboa.ewp.node.config.bootstrap.BootstrapProperties;
import pt.ulisboa.ewp.node.config.cnr.CnrProperties;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.config.plugins.PluginsProperties;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;
import pt.ulisboa.ewp.node.config.scheduling.SchedulingProperties;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;
import pt.ulisboa.ewp.node.config.sync.SyncProperties;
import pt.ulisboa.ewp.node.domain.utils.DatabaseProperties;
import pt.ulisboa.ewp.node.utils.bean.ParamNameProcessor;
import pt.ulisboa.ewp.node.utils.http.converter.xml.EwpNamespacePrefixMapper;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

@SpringBootApplication(scanBasePackages = {"pt.ulisboa.ewp.node"})
@EnableConfigurationProperties(
    value = {
      DatabaseProperties.class,
      BootstrapProperties.class,
      ManifestProperties.class,
      PluginsProperties.class,
      RegistryProperties.class,
      SecurityProperties.class,
      SchedulingProperties.class,
      CnrProperties.class,
      SyncProperties.class
    })
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@EnableCaching(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
@Import(ValidationAutoConfiguration.class)
public class EwpNodeApplication {

  public static void main(String[] args) {
    SpringApplication.run(EwpNodeApplication.class);
  }

  /** Returns a message source for internationalization (i18n). */
  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();

    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public Marshaller marshaller() throws JAXBException {
    return jaxb2Marshaller().getJaxbContext().createMarshaller();
  }

  /**
   * Inject a (un)marshalling HTTP message converter that is aware of the different packages to scan
   * when marshalling/unmarshalling.
   */
  @Bean
  public Jaxb2HttpMessageConverter marshallingHttpMessageConverter() {
    Jaxb2HttpMessageConverter result = new Jaxb2HttpMessageConverter();
    result.setPackagesToScan("eu.erasmuswithoutpaper.api", "pt.ulisboa.ewp.node");
    result.setSupportJaxbElementClass(true);
    result.setNamespacePrefixMapper(new EwpNamespacePrefixMapper());
    return result;
  }

  @Bean
  public Jaxb2Marshaller jaxb2Marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setPackagesToScan("eu.erasmuswithoutpaper.api", "pt.ulisboa.ewp.node");
    marshaller.setSupportJaxbElementClass(true);

    Map<String, Object> jaxbProperties = new HashMap<>();
    jaxbProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.setMarshallerProperties(jaxbProperties);
    return marshaller;
  }

  /**
   * Custom {@link BeanPostProcessor} for adding {@link ParamNameProcessor} into the first of {@link
   * RequestMappingHandlerAdapter#argumentResolvers}.
   *
   * @return BeanPostProcessor
   */
  @Bean
  public BeanPostProcessor beanPostProcessor() {
    return new BeanPostProcessor() {

      @Override
      public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
      }

      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof RequestMappingHandlerAdapter) {
          RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
          List<HandlerMethodArgumentResolver> argumentResolvers =
              new ArrayList<>(adapter.getArgumentResolvers());
          argumentResolvers.add(0, paramNameProcessor(adapter));
          adapter.setArgumentResolvers(argumentResolvers);
        }
        return bean;
      }
    };
  }

  /**
   * A processor for {@link pt.ulisboa.ewp.node.utils.bean.ParamName} annotations. Reference:
   * https://stackoverflow.com/questions/8986593/how-to-customize-parameter-names-when-binding-spring-mvc-command-objects
   */
  @Bean
  public ParamNameProcessor paramNameProcessor(
      RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
    return new ParamNameProcessor(requestMappingHandlerAdapter);
  }

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(false);
    loggingFilter.setIncludeQueryString(true);
    loggingFilter.setIncludeHeaders(false);
    loggingFilter.setIncludePayload(true);
    loggingFilter.setMaxPayloadLength(500);
    return loggingFilter;
  }

  @Bean
  @ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true")
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(5);
    threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
    threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
    threadPoolTaskScheduler.setAwaitTerminationSeconds(30);
    threadPoolTaskScheduler.setRejectedExecutionHandler(new AbortPolicy());
    return threadPoolTaskScheduler;
  }
}
