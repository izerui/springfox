package springfox.documentation.spring.web.readers.operation

import com.fasterxml.classmate.ResolvedType
import com.google.common.base.Predicate
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.schema.Category
import springfox.documentation.spi.service.contexts.RequestMappingContext
import springfox.documentation.spring.web.WebMvcRequestHandler
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec

@Mixin([RequestMappingSupport])
class OperationModelsProviderSpec extends DocumentationContextSpec {

  def "Should respect isomorphic types"() {
    given:
    RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
        [patternsRequestCondition: patternsRequestCondition('/somePath/{businessId}', '/somePath/{businessId:\\d+}')]
    )
    plugin.isomorphicTypesPredicate(new Predicate<ResolvedType>() {
      @Override
      boolean apply(ResolvedType input) {
        return Category.isAssignableFrom(input.erasedType)
      }
    })
    RequestMappingContext requestContext = new RequestMappingContext(
        context(),
        new WebMvcRequestHandler(
            requestMappingInfo,
            dummyHandlerMethod('methodWithIsomorphicType', Category.class)))
    OperationModelsProvider sut = new OperationModelsProvider()
    when:
    sut.apply(requestContext)
    def models = requestContext.operationModelsBuilder().build()

    then:
    models.size() == 3
  }
}
