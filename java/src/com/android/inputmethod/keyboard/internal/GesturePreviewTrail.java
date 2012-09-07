/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.android.inputmethod.keyboard.internal;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.os.SystemClock;

import com.android.inputmethod.latin.Constants;
import com.android.inputmethod.latin.R;
import com.android.inputmethod.latin.ResizableIntArray;

final class GesturePreviewTrail {
    private static final int DEFAULT_CAPACITY = GestureStrokeWithPreviewTrail.PREVIEW_CAPACITY;

    private final ResizableIntArray mXCoordinates = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mYCoordinates = new ResizableIntArray(DEFAULT_CAPACITY);
    private final ResizableIntArray mEventTimes = new ResizableIntArray(DEFAULT_CAPACITY);
    private int mCurrentStrokeId = -1;
    // The wall time of the zero value in {@link #mEventTimes}
    private long mCurrentTimeBase;
    private int mTrailStartIndex;

    static final class Params {
        public final int mTrailColor;
        public final float mTrailStartWidth;
        public final float mTrailEndWidth;
        public final int mFadeoutStartDelay;
        public final int mFadeoutDuration;
        public final int mUpdateInterval;

        public final int mTrailLingerDuration;

        public Params(final TypedArray keyboardViewAttr) {
            mTrailColor = keyboardViewAttr.getColor(
                    R.styleable.KeyboardView_gesturePreviewTrailColor, 0);
            mTrailStartWidth = keyboardViewAttr.getDimension(
                    R.styleable.KeyboardView_gesturePreviewTrailStartWidth, 0.0f);
            mTrailEndWidth = keyboardViewAttr.getDimension(
                    R.styleable.KeyboardView_gesturePreviewTrailEndWidth, 0.0f);
            mFadeoutStartDelay = keyboardViewAttr.getInt(
                    R.styleable.KeyboardView_gesturePreviewTrailFadeoutStartDelay, 0);
            mFadeoutDuration = keyboardViewAttr.getInt(
                    R.styleable.KeyboardView_gesturePreviewTrailFadeoutDuration, 0);
            mTrailLingerDuration = mFadeoutStartDelay + mFadeoutDuration;
            mUpdateInterval = keyboardViewAttr.getInt(
                    R.styleable.KeyboardView_gesturePreviewTrailUpdateInterval, 0);
        }
    }

    // Use this value as imaginary zero because x-coordinates may be zero.
    private static final int DOWN_EVENT_MARKER = -128;

    private static int markAsDownEvent(final int xCoord) {
        return DOWN_EVENT_MARKER - xCoord;
    }

    private static boolean isDownEventXCoord(final int xCoordOrMark) {
        return xCoordOrMark <= DOWN_EVENT_MARKER;
    }

    private static int getXCoordValue(final int xCoordOrMark) {
        return isDownEventXCoord(xCoordOrMark)
                ? DOWN_EVENT_MARKER - xCoordOrMark : xCoordOrMark;
    }

    public void addStroke(final GestureStrokeWithPreviewTrail stroke, final long downTime) {
        final int trailSize = mEventTimes.getLength();
        stroke.appendPreviewStroke(mEventTimes, mXCoordinates, mYCoordinates);
        if (mEventTimes.getLength() == trailSize) {
            return;
        }
        final int[] eventTimes = mEventTimes.getPrimitiveArray();
        final int strokeId = stroke.getGestureStrokeId();
        if (strokeId != mCurrentStrokeId) {
            final int elapsedTime = (int)(downTime - mCurrentTimeBase);
            for (int i = mTrailStartIndex; i < trailSize; i++) {
                // Decay the previous strokes' event times.
                eventTimes[i] -= elapsedTime;
            }
            final int[] xCoords = mXCoordinates.getPrimitiveArray();
            final int downIndex = trailSize;
            xCoords[downIndex] = markAsDownEvent(xCoords[downIndex]);
            mCurrentTimeBase = downTime - eventTimes[downIndex];
            mCurrentStrokeId = strokeId;
        }
    }

    private static int getAlpha(final int elapsedTime, final Params params) {
        if (elapsedTime < params.mFadeoutStartDelay) {
            return Constants.Color.ALPHA_OPAQUE;
        }
        final int decreasingAlpha = Constants.Color.ALPHA_OPAQUE
                * (elapsedTime - params.mFadeoutStartDelay)
                / params.mFadeoutDuration;
        return Constants.Color.ALPHA_OPAQUE - decreasingAlpha;
    }

    private static float getWidth(final int elapsedTime, final Params params) {
        return Math.max((params.mTrailLingerDuration - elapsedTime)
                * (params.mTrailStartWidth - params.mTrailEndWidth)
                / params.mTrailLingerDuration, 0.0f);
    }

    private final static Xfermode PORTER_DUFF_MODE_SRC =
            new PorterDuffXfermode(PorterDuff.Mode.SRC);

    /**
     * Draw gesture preview trail
     * @param canvas The canvas to draw the gesture preview trail
     * @param paint The paint object to be used to draw the gesture preview trail
     * @param params The drawing parameters of gesture preview trail
     * @return true if some gesture preview trails remain to be drawn
     */
    public boolean drawGestureTrail(final Canvas canvas, final Paint paint, final Params params) {
        final int trailSize = mEventTimes.getLength();
        if (trailSize == 0) {
            return false;
        }

        final int[] eventTimes = mEventTimes.getPrimitiveArray();
        final int[] xCoords = mXCoordinates.getPrimitiveArray();
        final int[] yCoords = mYCoordinates.getPrimitiveArray();
        final int sinceDown = (int)(SystemClock.uptimeMillis() - mCurrentTimeBase);
        int startIndex;
        for (startIndex = mTrailStartIndex; startIndex < trailSize; startIndex++) {
            final int elapsedTime = sinceDown - eventTimes[startIndex];
            // Skip too old trail points.
            if (elapsedTime < params.mTrailLingerDuration) {
                break;
            }
        }
        mTrailStartIndex = startIndex;

        if (startIndex < trailSize) {
            int lastX = getXCoordValue(xCoords[startIndex]);
            int lastY = yCoords[startIndex];
            paint.setColor(params.mTrailColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setXfermode(PORTER_DUFF_MODE_SRC);
            for (int i = startIndex + 1; i < trailSize - 1; i++) {
                final int x = xCoords[i];
                final int y = yCoords[i];
                final int elapsedTime = sinceDown - eventTimes[i];
                // Draw trail line only when the current point isn't a down point.
                if (!isDownEventXCoord(x)) {
                    final int alpha = getAlpha(elapsedTime, params);
                    paint.setAlpha(alpha);
                    final float width = getWidth(elapsedTime, params);
                    paint.setStrokeWidth(width);
                    canvas.drawLine(lastX, lastY, x, y, paint);
                }
                lastX = getXCoordValue(x);
                lastY = y;
            }
        }

        final int newSize = trailSize - startIndex;
        if (newSize < startIndex) {
            mTrailStartIndex = 0;
            if (newSize > 0) {
                System.arraycopy(eventTimes, startIndex, eventTimes, 0, newSize);
                System.arraycopy(xCoords, startIndex, xCoords, 0, newSize);
                System.arraycopy(yCoords, startIndex, yCoords, 0, newSize);
            }
            mEventTimes.setLength(newSize);
            mXCoordinates.setLength(newSize);
            mYCoordinates.setLength(newSize);
        }
        return newSize > 0;
    }
}
