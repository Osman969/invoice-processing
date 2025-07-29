package it.arrive.invoicesystem.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties ( prefix = "arrive.app.pagination" )
@Component
@Getter
@Setter
public class PaginationProperties {

    private int defaultPageSize = 100;
}