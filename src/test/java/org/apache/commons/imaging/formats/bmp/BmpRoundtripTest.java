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

package org.apache.commons.imaging.formats.bmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.internal.Debug;
import org.junit.jupiter.api.Test;

class BmpRoundtripTest extends AbstractBmpTest {

    private int[][] bufferedImageToImageData(final BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int[][] result = new int[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[y][x] = image.getRGB(x, y);
            }
        }
        return result;
    }

    private void compare(final int[][] a, final int[][] b) {
        assertNotNull(a);
        assertNotNull(b);
        assertEquals(a.length, b.length);

        for (int y = 0; y < a.length; y++) {
            assertEquals(a[y].length, b[y].length);
            // make sure row lengths consistent.
            assertEquals(a[0].length, b[y].length);
            for (int x = 0; x < a[y].length; x++) {
                // ignore alpha channel - BMP has no transparency.
                final int rgbA = 0xffffff & a[y][x];
                final int rgbB = 0xffffff & b[y][x];

                if (rgbA != rgbB) {
                    Debug.debug("x: " + x + ", y: " + y + ", rgbA: " + rgbA + " (0x" + Integer.toHexString(rgbA) + "), rgbB: " + rgbB + " (0x"
                            + Integer.toHexString(rgbB) + ")");
                }
                assertEquals(rgbA, rgbB);
            }
        }
    }

    private int[][] getAscendingRawData(final int width, final int height) {
        final int[][] result = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int alpha = (x + y) % 256;
                final int value = (x + y) % 256;
                final int argb = (0xff & alpha) << 24 | (0xff & value) << 16 | (0xff & value) << 8 | (0xff & value) << 0;

                result[y][x] = argb;
            }
        }
        return result;
    }

    private int[][] getSimpleRawData(final int width, final int height, final int value) {
        final int[][] result = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[y][x] = value;
            }
        }
        return result;
    }

    private BufferedImage imageDataToBufferedImage(final int[][] rawData) {
        final int width = rawData[0].length;
        final int height = rawData.length;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, rawData[y][x]);
            }
        }
        return image;
    }

    private int[][] randomRawData(final int width, final int height) {
        final Random random = new Random();
        final int[][] result = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int argb = random.nextInt();
                result[y][x] = argb;
            }
        }
        return result;
    }

    @Test
    void testSingleBlackPixel() throws Exception {
        final int[][] singleBlackPixel = getSimpleRawData(1, 1, 0);
        writeAndReadImageData(singleBlackPixel);
    }

    @Test
    void testSingleRedPixel() throws Exception {
        final int[][] singleRedPixel = getSimpleRawData(1, 1, 0xffff0000);
        writeAndReadImageData(singleRedPixel);
    }

    @Test
    void testSmallAscendingPixels() throws Exception {
        final int[][] smallAscendingPixels = getAscendingRawData(256, 256);
        writeAndReadImageData(smallAscendingPixels);
    }

    @Test
    void testSmallBlackPixels() throws Exception {
        final int[][] smallBlackPixels = getSimpleRawData(256, 256, 0);
        writeAndReadImageData(smallBlackPixels);
    }

    @Test
    void testSmallRandomPixels() throws Exception {
        final int[][] smallRandomPixels = randomRawData(256, 256);
        writeAndReadImageData(smallRandomPixels);
    }

    @Test
    void testSmallRedPixels() throws Exception {
        final int[][] smallRedPixels = getSimpleRawData(256, 256, 0xffff0000);
        writeAndReadImageData(smallRedPixels);
    }

    private void writeAndReadImageData(final int[][] rawData) throws IOException, ImagingException, ImagingException {
        final BufferedImage srcImage = imageDataToBufferedImage(rawData);

        final byte[] bytes = Imaging.writeImageToBytes(srcImage, ImageFormats.BMP);

        final BufferedImage dstImage = Imaging.getBufferedImage(bytes);

        assertNotNull(dstImage);
        assertEquals(srcImage.getWidth(), dstImage.getWidth());
        assertEquals(srcImage.getHeight(), dstImage.getHeight());

        final int[][] dstData = bufferedImageToImageData(dstImage);
        compare(rawData, dstData);
    }

}
