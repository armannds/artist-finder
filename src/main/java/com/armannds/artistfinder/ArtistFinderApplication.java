package com.armannds.artistfinder;

import com.armannds.artistfinder.api.coverartarchive.CoverArtArchiveService;
import com.armannds.artistfinder.api.musicbrainz.MusicBrainzService;
import com.armannds.artistfinder.api.wikipedia.WikipediaService;
import com.armannds.artistfinder.service.ArtistService;
import com.armannds.artistfinder.service.CoverIconService;
import com.armannds.artistfinder.service.DescriptionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
    public RestTemplate restTemplate() {
		return new RestTemplate();
    }

    @Bean
    public DescriptionService descriptionService(RestTemplate restTemplate) {
	    return new WikipediaService(restTemplate);
	}

    @Bean
	CoverIconService coverIconService(RestTemplate restTemplate) {
		return new CoverArtArchiveService(restTemplate);
    }

    @Bean
    public ArtistService artistService(RestTemplate restTemplate) {
	    return new MusicBrainzService(restTemplate);
    }
}