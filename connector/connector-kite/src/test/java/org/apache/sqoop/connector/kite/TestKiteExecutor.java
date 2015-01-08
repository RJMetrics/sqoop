/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.connector.kite;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kitesdk.data.Dataset;
import org.kitesdk.data.DatasetDescriptor;
import org.kitesdk.data.DatasetReader;
import org.kitesdk.data.DatasetWriter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestKiteExecutor {

  @org.mockito.Mock
  private Dataset<GenericRecord> datasetMock;

  @org.mockito.Mock
  private DatasetDescriptor descriptorMock;

  @org.mockito.Mock
  private DatasetWriter<GenericRecord> writerMock;

  @org.mockito.Mock
  private DatasetReader<GenericRecord> readerMock;

  private KiteDatasetExecutor executor;

  @Before
  public void setUp() {
    initMocks(this);
    when(datasetMock.newWriter()).thenReturn(writerMock);
    when(datasetMock.newReader()).thenReturn(readerMock);
    when(datasetMock.getDescriptor()).thenReturn(descriptorMock);
    when(descriptorMock.getSchema()).thenReturn(
        new Schema.Parser().parse("{\"name\":\"test\",\"type\":\"record\"," +
            "\"fields\":[]}"));

    executor = new KiteDatasetExecutor(datasetMock);
  }

  @After
  public void tearDown() {
    executor.closeWriter();
    executor.closeReader();
    assertTrue(executor.isWriterClosed());
    assertTrue(executor.isReaderClosed());
  }

  @Test
  public void testWriteRecord() {
    // setup & exercise
    final int NUMBER_OF_ROWS = 10;
    createDatasetWithRecords(NUMBER_OF_ROWS);

    // verify
    verify(writerMock, times(NUMBER_OF_ROWS)).write(any(GenericRecord.class));
    verifyNoMoreInteractions(writerMock);
  }

  @Test
  public void testCloseWriter() {
    // setup
    when(writerMock.isOpen()).thenReturn(true);
    executor.writeRecord(new Object[]{});
    assertTrue(!executor.isWriterClosed());

    // exercise
    executor.closeWriter();

    // verify
    verify(writerMock, times(1)).close();
    assertTrue(executor.isWriterClosed());
  }

  @Test
  public void testReaderRecord() {
    // setup
    final int NUMBER_OF_ROWS = 10;
    createDatasetWithRecords(NUMBER_OF_ROWS);
    when(readerMock.next()).thenReturn(
        new GenericRecordBuilder(createTwoFieldSchema())
            .set("f1", 1)
            .set("f2", "foo")
            .build());
    when(readerMock.hasNext()).thenReturn(true);

    // exercise & verify
    for (int i = 0; i < NUMBER_OF_ROWS; i++) {
      Object[] actual = executor.readRecord();
      assertNotNull(actual);
      assertEquals(2, actual.length);
      assertEquals(1, actual[0]);
      assertEquals("foo", actual[1]);
    }
    when(readerMock.hasNext()).thenReturn(false);
    Object[] actual = executor.readRecord();
    assertNull(actual);
  }

  @Test
  public void testCloseReader() {
    // setup
    when(readerMock.isOpen()).thenReturn(true);
    executor.readRecord();
    assertTrue(!executor.isReaderClosed());

    // exercise
    executor.closeReader();

    // verify
    verify(readerMock, times(1)).close();
    assertTrue(executor.isReaderClosed());
  }

  private static Schema createTwoFieldSchema() {
    return new Schema.Parser().parse("{" +
        "\"name\":\"test\",\"type\":\"record\"," +
        "\"fields\":[" +
        "{\"name\":\"f1\",\"type\":\"int\"}," +
        "{\"name\":\"f2\",\"type\":\"string\"}" +
        "]}");
  }

  private void createDatasetWithRecords(int numberOfRecords) {
    when(descriptorMock.getSchema()).thenReturn(createTwoFieldSchema());

    // exercise
    for (int i = 0; i < numberOfRecords; i++) {
      executor.writeRecord(new Object[]{i, "foo" + i});
    }
  }

}