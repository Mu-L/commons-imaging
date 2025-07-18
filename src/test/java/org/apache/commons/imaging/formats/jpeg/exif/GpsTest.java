/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.imaging.formats.jpeg.exif;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.stream.Stream;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.internal.Debug;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class GpsTest extends AbstractExifTest {

    public static Stream<File> data() throws Exception {
        return getImagesWithExifData().stream();
    }

    @ParameterizedTest
    @MethodSource("data")
    void test(final File imageFile) throws Exception {
        if (imageFile.getParentFile().getName().toLowerCase().equals("@broken")) {
            return;
        }

        final JpegImageMetadata metadata = (JpegImageMetadata) Imaging.getMetadata(imageFile);
        if (null == metadata) {
            return;
        }

        final TiffImageMetadata exifMetadata = metadata.getExif();
        if (null == exifMetadata) {
            return;
        }

        final TiffImageMetadata.GpsInfo gpsInfo = exifMetadata.getGpsInfo();
        if (null == gpsInfo) {
            return;
        }

        // TODO we should assert something here.
        Debug.debug("imageFile " + imageFile);
        Debug.debug("gpsInfo " + gpsInfo);
        Debug.debug("gpsInfo longitude as degrees east " + gpsInfo.getLongitudeAsDegreesEast());
        Debug.debug("gpsInfo latitude as degrees north " + gpsInfo.getLatitudeAsDegreesNorth());
        Debug.debug();

    }

    /**
     * @throws Exception if it cannot open the images.
     */
    @Test
    void testReadMetadata() throws Exception {
        final File imageFile = new File(GpsTest.class.getResource("/images/jpeg/exif/2024-04-30_G012.JPG").getFile());
        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) Imaging.getMetadata(imageFile);
        final TiffField gpsHPosErrorField = jpegMetadata.findExifValueWithExactMatch(GpsTagConstants.GPS_TAG_GPS_HOR_POSITIONING_ERROR);
        final RationalNumber gpsHPosError = (RationalNumber) gpsHPosErrorField.getValue();
        assertEquals(0.014, gpsHPosError.doubleValue());
    }
}
