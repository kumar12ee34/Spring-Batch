package com.example.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.example.model.User;

@Configuration
public class SpringBatchConfig {

	@Bean
	public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			ItemReader<User> itemReader, ItemProcessor<User, User> itemProcessor, ItemWriter<User> itemWriter) {
		
		Step step = stepBuilderFactory.get("ETL-file-Load")
					.<User, User>chunk(100)
					.reader(itemReader)
					.processor(itemProcessor)
					.writer(itemWriter)
					.build();
		
		Job job = jobBuilderFactory.get("ETL-Load")
		.incrementer(new RunIdIncrementer())
		.start(step)
		.build();
		return job;
	}
	
	@Bean
	public FlatFileItemReader<User> fileItemReader(@Value("${input}") Resource resource){
		FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(resource);
		flatFileItemReader.setName("CSV-Reader");
		flatFileItemReader.setLinesToSkip(1);
		flatFileItemReader.setLineMapper(lineMapper());
		return flatFileItemReader;
	}
	
	@Bean
	public LineMapper<User> lineMapper(){
		DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
		lineTokenizer.setDelimiter(",");
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames(new String[] {"id","name","dept","salary"});
		
		BeanWrapperFieldSetMapper<User> fieldMapper = new BeanWrapperFieldSetMapper<>();
		fieldMapper.setTargetType(User.class);
		
		defaultLineMapper.setLineTokenizer(lineTokenizer);
		defaultLineMapper.setFieldSetMapper(fieldMapper);
		
		return defaultLineMapper;
		
		
	}

}
