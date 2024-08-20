package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.io.IOException;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) throws IOException {
        String parquetFile = "/Users/eknarong.aph/Documents/me/project/poc-dev-programming-language/java-kotlin/poc-apache-parquet/src/main/resources/data.parquet";
//        String schemaJson = "{ \"type\" : \"record\", \"name\" : \"User\", \"fields\" : [" +
//                "{ \"name\" : \"name\", \"type\" : \"string\" }," +
//                "{ \"name\" : \"age\", \"type\" : \"int\" }" +
//                "] }";

        String schemaJson = "{  \"type\" : \"record\",  \"name\" : \"CustomFieldValue\",  \"fields\" : [ {    \"name\" : \"AMOUNT\",    \"type\" : [ \"null\", {      \"type\" : \"bytes\",      \"logicalType\" : \"decimal\",      \"precision\" : 38,      \"scale\" : 18    } ],    \"default\" : null  }, {    \"name\" : \"CUSTOMFIELDKEY\",    \"type\" : \"string\"  }, {    \"name\" : \"CUSTOMFIELDSETGROUPINDEX\",    \"type\" : \"long\"  }, {    \"name\" : \"ENCODEDKEY\",    \"type\" : \"string\"  }, {    \"name\" : \"INDEXINLIST\",    \"type\" : [ \"null\", \"long\" ],    \"default\" : null  }, {    \"name\" : \"LINKEDENTITYKEYVALUE\",    \"type\" : [ \"null\", \"string\" ],    \"default\" : null  }, {    \"name\" : \"PARENTKEY\",    \"type\" : \"string\"  }, {    \"name\" : \"VALUE\",    \"type\" : [ \"null\", \"string\" ],    \"default\" : null  }, {    \"name\" : \"OP\",    \"type\" : \"string\"  }, {    \"name\" : \"EVENT_CREATED_AT\",    \"type\" : {      \"type\" : \"long\",      \"logicalType\" : \"timestamp-millis\"    }  } ]}";

        // Define Avro schema
        Schema schema = new Schema.Parser().parse(schemaJson);

        // Write a Parquet file
        writeParquetFile(parquetFile, schema);

        // Read the Parquet file
        readParquetFile(parquetFile);
    }

    private static void writeParquetFile(String parquetFile, Schema schema) throws IOException {
        Path path = new Path(parquetFile);
        ParquetWriter<GenericRecord> writer = AvroParquetWriter.<GenericRecord>builder(path)
                .withSchema(schema)
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .withConf(new Configuration())
                .withPageSize(1024) // in bytes
                .withRowGroupSize(1024 * 1024) // in bytes
                .build();

        // Create a record
        GenericRecord record = new GenericData.Record(schema);
        record.put("AMOUNT", "30.00234".getBytes());
        record.put("CUSTOMFIELDKEY", "8a8e8777765158fd01765622bb13114d");
        record.put("CUSTOMFIELDSETGROUPINDEX", -1);
        record.put("ENCODEDKEY", "8af822e89135e89c01913666a39908bf");
        record.put("INDEXINLIST", -1);
        record.put("LINKEDENTITYKEYVALUE", null);
        record.put("PARENTKEY", "8af822e89135e89c01913666a39908bb");
        record.put("VALUE", "PAYLATER");
        record.put("OP", "d");
        record.put("EVENT_CREATED_AT", System.currentTimeMillis());

        writer.write(record);
        writer.close();
    }

    private static void readParquetFile(String parquetFile) throws IOException {
        Path path = new Path(parquetFile);
        ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(path).build();

        GenericRecord record;
        while ((record = reader.read()) != null) {
            System.out.println(record.getSchema());
            System.out.println(record);
        }

        reader.close();
    }
}