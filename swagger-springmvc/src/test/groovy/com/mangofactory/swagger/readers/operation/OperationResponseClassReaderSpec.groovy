package com.mangofactory.swagger.readers.operation
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class OperationResponseClassReaderSpec extends Specification {

   def "should have correct response class"() {

    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      def settings = new SwaggerGlobalSettings()
      SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()
    settings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(new TypeResolver())

    context.put("swaggerGlobalSettings", settings)
      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader()

    when:
      operationResponseClassReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['responseClass'] == expectedClass
    where:
      handlerMethod                                                        | expectedClass
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'
      dummyHandlerMethod('methodApiResponseClass')                         | 'FunkyBusiness'
      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'
      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'

   }
}
