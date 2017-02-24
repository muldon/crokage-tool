package com.ufu;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ufu.bot.service.TagsService;
import com.ufu.bot.to.Tags;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BotApplicationTests {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TagsService tagsService;
	@Test
	public void contextLoads() {
	}

	@Test
	public void getTags() {
		List<Tags> tags = tagsService.loadAll();
		for (Tags tag : tags) {
			logger.info(tag.toString());
		}
		// perspectivaService.saveAll(perspectivas);

	}

}
