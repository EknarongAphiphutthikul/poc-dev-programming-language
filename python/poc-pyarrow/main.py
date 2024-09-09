import io
from datetime import datetime
from decimal import Decimal

import pyarrow as pa
import pyarrow.parquet as pq
import pyarrow.csv as csv
import pyarrow.compute as pp
import pyarrow.lib as pl


def read_parquet_schema(full_path: str) -> pa.Schema:
    data_table = pq.read_table(full_path)
    return data_table.schema


def read_csv_table(full_path: str, convert_options: csv.ConvertOptions = None) -> pa.Table:
    return csv.read_csv(full_path, convert_options=convert_options)


def build_csv_convert_options(parquet_schema: pa.Schema) -> csv.ConvertOptions:
    parquet_schema_column = parquet_schema.names
    parquet_schema_types = parquet_schema.types
    dist_column_types = {}
    for index in range(len(parquet_schema_column)):
        column_name = parquet_schema_column[index]
        column_type = parquet_schema_types[index]
        dist_column_types[column_name] = column_type
    return csv.ConvertOptions(
        column_types=dist_column_types
    )


def write_parquet_file(csv_data: pa.Table, full_path: str):
    buffer = io.BytesIO()
    pq.write_table(csv_data, buffer)
    parquet_bytearray = buffer.getvalue()
    parquet_bytearray = bytearray(parquet_bytearray)
    with open(full_path, 'wb') as f:
        f.write(parquet_bytearray)
    # with pq.ParquetWriter(full_path, csv_data.schema) as writer:
    #     writer.write_table(csv_data)


def convert_csv_to_parquet_file(input_parquet_file_path: str, input_csv_file_path: str, output_parquet_file_path: str):
    parquet_schema = read_parquet_schema(input_parquet_file_path)
    csv_convert_options = build_csv_convert_options(parquet_schema)
    csv_table = read_csv_table(input_csv_file_path, csv_convert_options)
    write_parquet_file(csv_table, output_parquet_file_path)


convert_csv_to_parquet_file('customfieldvalue-0-2999522-3000365.parquet', 'mambu_customfieldvalue.csv', 'customfieldvalue-0-0000000-0000001.parquet')

# # --------------------------------------------------------------------------------------------------

# parquet_file = pq.ParquetFile('customfieldvalue-0-2999522-3000365.parquet')
# print("Schema:")
# print(parquet_file.schema)

# parquet_file = pq.read_table('customfieldvalue-0-2999522-3000365.parquet')
# print("Schema:")
# print(parquet_file.schema)
#
# # Step 1: Define a schema
# schema = parquet_file.schema
# # schema = parquet_file.schema_arrow
#
#
# csv_file = pc.read_csv('mambu_customfieldvalue.csv', convert_options=pc.ConvertOptions(
#     column_types={
#         'AMOUNT': pa.decimal128(precision=38, scale=18)
#     })
#                        )
# print("\n\n ====== Data before cast ======")
# print(csv_file)
#
# csv_file = csv_file.cast(schema)
#
# print("\n\n ====== Data after cast ======")
# print(csv_file)
#
# # write to parquet_file
# with pq.ParquetWriter('customfieldvalue-0-0000000-0000001.parquet', schema) as writer:
#     writer.write_table(csv_file)

# # --------------------------------------------------------------------------------------------------

# Step 2: Create data as a list of tuples
# data = [
#     (30, '8a8e8777765158fd01765622bb13114d', -1, '8af822e89135e89c01913666a39908bf', -1, None, '8af822e89135e89c01913666a39908bb', 'PAYLATER','d',datetime.now())
# ]

# Step 3: Convert data to PyArrow table
# table = pa.Table.from_pydict({
#     'AMOUNT': [item[0] for item in data],
#     'CUSTOMFIELDKEY': [item[1] for item in data],
#     'CUSTOMFIELDSETGROUPINDEX': [item[2] for item in data],
#     'ENCODEDKEY': [item[3] for item in data],
#     'INDEXINLIST': [item[4] for item in data],
#     'LINKEDENTITYKEYVALUE': [item[5] for item in data],
#     'PARENTKEY': [item[6] for item in data],
#     'VALUE': [item[7] for item in data],
#     'OP': [item[8] for item in data],
#     'EVENT_CREATED_AT': [item[9] for item in data],
# }, schema=schema)

# Step 4: Write the table to a Parquet file
# with pq.ParquetWriter('output.parquet', schema) as writer:
#     writer.write_table(table)
#     writer.write()

# # --------------------------------------------------------------------------------------------------
# # Step 1: Define a schema
# schema = pa.schema([
#     ('id', pa.int32()),
#     ('name', pa.string()),
#     ('age', pa.int32())
# ])
#
# # Step 2: Create data as a list of tuples
# data = [
#     (1, 'Alice', 25),
#     (2, 'Bob', 30),
#     (3, 'Charlie', 35)
# ]
#
# # Step 3: Convert data to PyArrow table
# table = pa.Table.from_pydict({
#     'id': [item[0] for item in data],
#     'name': [item[1] for item in data],
#     'age': [item[2] for item in data]
# }, schema=schema)
#
# # Step 4: Write the table to a Parquet file
# with pq.ParquetWriter('output.parquet', schema) as writer:
#     writer.write_table(table)
#
# print(schema)
# print("Parquet file created successfully.")
