package com.ameshi.spring_batch_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;
public class DataItemProcessor implements ItemProcessor<Item, Item> {

    private static final Logger log = LoggerFactory.getLogger(DataItemProcessor.class);

    @Override
    public Item process(final Item item) {
        final String data = item.data();
        final String deviceType = item.device();
        final String language = item.language();
        final String msisdn = item.msisdn();

        final Item transformedPerson = new Item(data, deviceType, language, msisdn);

        log.info("Converting (" + item + ") into (" + transformedPerson + ")");

        return transformedPerson;
    }
}
