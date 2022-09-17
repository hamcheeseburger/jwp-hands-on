package com.example.etag;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import java.util.List;

import static com.example.version.CacheBustingWebConfig.PREFIX_STATIC_RESOURCES;

@Configuration
public class EtagFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
        /* ConditionalContentCachingResponseWrapper로 Response를 Wrapping하여 doFilter를 진행하고
           ContentCaching 가능 여부에 따라 응답값에 따른 eTag를 생성하여 Response 헤더에 추가해 주는 필터이다.
           ConditionalContentCachingResponseWrapper 는 ContentCachingDisabled 인 경우 캐싱을 수행하는 출력 스트림 대신 원시 출력 스트림을 반환한다.
        */
        final ShallowEtagHeaderFilter shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
        // 필터를 등록하는 빈이며, 해당 필터가 어떤 Url 패턴에서 동작할 것인지 지정할 수 있다.
        final FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>(shallowEtagHeaderFilter);
        filterRegistrationBean.setUrlPatterns(List.of("/etag", PREFIX_STATIC_RESOURCES + "/*"));
        return filterRegistrationBean;
    }
}
