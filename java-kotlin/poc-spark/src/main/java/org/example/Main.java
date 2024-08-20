package org.example;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        SparkSession spark = SparkSession
                .builder()
                .appName("Java Spark SQL data sources example")
                .config("spark.master", "local")
//                .config("spark.sql.parquet.int96AsTimestamp", false)
                .config("spark.sql.parquet.writeLegacyFormat", true)
                .config("spark.sql.parquet.outputTimestampType", "TIMESTAMP_MILLIS")
                .getOrCreate();

//        runBasicPOC(spark);
//        pocDeciaml(spark);

        avropoc();

//        pocDeciamlAvro(spark);

        spark.stop();
    }



    private static void runBasicPOC(SparkSession spark) {
        Dataset<Row> usersDF = spark.read().format("parquet").load("src/main/resources/customfieldvalue-0-2999522-3000365.parquet");
        // spark read schema from parquet file
        usersDF.printSchema();

        StructType stType = usersDF.schema();
        StructField[] fields = stType.fields();
        for (StructField field : fields) {
            System.out.println(String.format("%s  -  %s", field.name(), field.dataType().typeName()));
            field.metadata().map().keySet().foreach(key -> {
                System.out.println(String.format("  %s  -  %s", key, field.metadata().getString(key)));
                return null;
            });
        }



        usersDF.show();

        usersDF.write()
                .format("parquet")
                .save("src/main/resources/customfieldvalue-1.parquet");

    }

    public static void pocDeciaml(SparkSession spark) {
        List<Row> data = new ArrayList<>();
        BigDecimal decimalValue = new BigDecimal("1234.5678");

        byte[] decimalAsBytes = decimalValue.unscaledValue().toByteArray();
        data.add(RowFactory.create(decimalValue));

        StructType schema = new StructType(new StructField[]{
                DataTypes.createStructField("decimal_as_bytes", DataTypes.createDecimalType(38, 18), true)
        });

        Dataset<Row> df = spark.createDataFrame(data, schema);

        df.write().parquet("src/main/resources/poc-decimal-to-byte.parquet");
    }

    public static void pocDeciamlAvro(SparkSession spark) {
        List<Row> data = new ArrayList<>();
        BigDecimal decimalValue = new BigDecimal("1234.5678");

        byte[] decimalAsBytes = decimalValue.unscaledValue().toByteArray();
        data.add(RowFactory.create(decimalValue));

        StructType schema = new StructType(new StructField[]{
                DataTypes.createStructField("decimal_as_bytes", DataTypes.createDecimalType(38, 18), true)
        });

        Dataset<Row> df = spark.createDataFrame(data, schema);

        df.write().format("avro").save("src/main/resources/poc-decimal-to-byte.avro");
    }

    public static void avropoc() {
        // Define the schema
        Schema schema = SchemaBuilder.record("CustomFieldValue")
                .fields()
                .name("AMOUNT").type().unionOf().nullType().and()
                .bytesType().endUnion().noDefault()
                .endRecord();

        try {
            // Create a record using the schema
            GenericRecord record1 = new GenericData.Record(schema);
            GenericRecord record2 = new GenericData.Record(schema);

            // Convert BigDecimal to byte array as Avro expects decimals to be stored as bytes
            BigDecimal decimalValue = new BigDecimal("1234.567800000000000000");
            byte[] decimalBytes = bigDecimalToBytes(decimalValue, 38, 18);

            record1.put("AMOUNT", ByteBuffer.wrap(decimalBytes)); // Storing decimal as bytes
            record2.put("AMOUNT", null); // Null value for AMOUNT

            // Write records to Avro file
            File file = new File("src/main/resources/apache-avro-poc-decimal-to-byte.avro");
            GenericDatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema);
            DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(datumWriter);
            dataFileWriter.create(schema, file);
            dataFileWriter.append(record1);
            dataFileWriter.append(record2);
            dataFileWriter.close();

            System.out.println("Avro file written successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to convert BigDecimal to bytes for Avro storage
    public static byte[] bigDecimalToBytes(BigDecimal bigDecimal, int precision, int scale) {
        BigInteger unscaledValue = bigDecimal.unscaledValue();
        return unscaledValue.toByteArray();
    }

}