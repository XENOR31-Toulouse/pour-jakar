package com.omenaapp.worksite_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.omenaapp.worksite_service.application.service.AdminWorksiteService;
import com.omenaapp.worksite_service.application.service.MyWorksitesService;
import com.omenaapp.worksite_service.application.service.WorksiteActivityService;
import com.omenaapp.worksite_service.domain.port.out.AssignmentRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.ProgressUpdateRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorkEventRepositoryPort;
import com.omenaapp.worksite_service.domain.port.out.WorksiteRepositoryPort;

@Configuration
public class ApplicationServicesConfig {

    @Bean
    AdminWorksiteService adminWorksiteService(
        WorksiteRepositoryPort worksites,
        AssignmentRepositoryPort assignments
    ) {
        return new AdminWorksiteService(worksites, assignments);
    }

    @Bean
    MyWorksitesService myWorksitesService(
        AssignmentRepositoryPort assignments,
        WorksiteRepositoryPort worksites
    ) {
        return new MyWorksitesService(assignments, worksites);
    }

    @Bean
    WorksiteActivityService worksiteActivityService(
        WorksiteRepositoryPort worksites,
        AssignmentRepositoryPort assignments,
        WorkEventRepositoryPort events,
        ProgressUpdateRepositoryPort progress
    ) {
        return new WorksiteActivityService(worksites, assignments, events, progress);
    }
}
