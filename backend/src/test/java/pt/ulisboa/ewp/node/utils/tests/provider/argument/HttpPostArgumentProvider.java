package pt.ulisboa.ewp.node.utils.tests.provider.argument;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.http.HttpMethod;

public class HttpPostArgumentProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
    return Stream.of(Arguments.of(HttpMethod.POST));
  }
}
