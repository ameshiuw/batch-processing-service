package com.ameshi.spring_batch_service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    @Bean
    public FlatFileItemReader<Item> reader() {
        return new FlatFileItemReaderBuilder<Item>()
                .name("dataItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .delimiter(",")
                .names(new String[]{"data", "device", "language", "msisdn"})
                .targetType(Item.class)
                .strict(false)
                .build();
    }

    @Bean
    public DataItemProcessor processor() {

        return new DataItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Item> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Item>()
                .sql("INSERT INTO data (data, device, language, msisdn) VALUES (:data, :device, :language, :msisdn)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository,Step step1, JobCompletionNotificationListener listener) {
        return new JobBuilder("importUserJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<Item> reader, DataItemProcessor processor, JdbcBatchItemWriter<Item> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Item, Item> chunk(5, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
