package com.armannds.artistfinder;

import com.armannds.artistfinder.api.coverArtArchive.CoverArtArchiveService;
import com.armannds.artistfinder.api.musicBrainz.MusicBrainzService;
import com.armannds.artistfinder.api.wikipedia.WikipediaService;
import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.CoverIconService;
import com.armannds.artistfinder.service.DescriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
public class ArtistFinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtistFinderApplication.class, args);
	}

	//Just using SimpleAsyncTaskExecutor for now, we can see after this iteration how to improve the taskExecutor
	@Bean
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}

	@Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public ObjectMapper jsonObjectMapper() {return new ObjectMapper();}

    @Bean
    public DescriptionService descriptionService(RestTemplate restTemplate, ObjectMapper jsonObjectMapper) {
	    return new WikipediaService(restTemplate, jsonObjectMapper);
	}

    @Bean
	CoverIconService coverIconService(RestTemplate restTemplate, ObjectMapper jsonObjectMapper) {
		return new CoverArtArchiveService(restTemplate, jsonObjectMapper);
    }

    @Bean
    public ArtistService artistService(RestTemplate restTemplate, DescriptionService descriptionService, CoverIconService coverIconService) {
	    return new MusicBrainzService(restTemplate, descriptionService, coverIconService);
    }
}