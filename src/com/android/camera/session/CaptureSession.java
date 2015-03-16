/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.session;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import com.android.camera.app.MediaSaver.OnMediaSavedListener;
import com.android.camera.exif.ExifInterface;
import com.android.camera.stats.CaptureSessionStatsCollector;
import com.android.camera.util.Size;

/**
 * A session is an item that is in progress of being created and saved, such as
 * a photo sphere or HDR+ photo.
 */
public interface CaptureSession {

    /** Classes implementing this interface can produce a capture session. */
    public static interface CaptureSessionCreator {
        /** Creates and starts a new capture session. */
        public CaptureSession createAndStartEmpty();
    }

    /**
     * Classes implementing this interface can listen to progress updates of
     * this session.
     */
    public static interface ProgressListener {
        /**
         * Called when the progress is changed.
         *
         * @param progressPercent The current progress in percent.
         */
        public void onProgressChanged(int progressPercent);

        /**
         * Called when the progress message is changed.
         *
         * @param messageId The current progress message ID.
         */
        public void onStatusMessageChanged(int messageId);
    }

    /** Returns the title/name of this session. */
    public String getTitle();

    /** Returns the location of this session or null. */
    public Location getLocation();

    /** Sets the location of this session. */
    public void setLocation(Location location);

    /**
     * Set the progress in percent for the current session. If set to or left at
     * 0, no progress bar is shown.
     */
    public void setProgress(int percent);

    /**
     * Returns the progress of this session in percent.
     */
    public int getProgress();

    /**
     * Returns the current progress message.
     */
    public int getProgressMessageId();

    /**
     * Changes the progress status message of this session.
     *
     * @param messageId the ID of the new message
     */
    public void setProgressMessage(int messageId);

    /**
     * For an ongoing session, this updates the currently displayed thumbnail.
     *
     * @param bitmap the thumbnail to be shown while the session is in progress.
     */
    public void updateThumbnail(Bitmap bitmap);

    /**
     * For an ongoing session, this updates the capture indicator thumbnail.
     *
     * @param bitmap the thumbnail to be shown while the session is in progress.
     *            update the capture indicator
     * @param rotationDegrees the rotation of the thumbnail in degrees
     */
    public void updateCaptureIndicatorThumbnail(Bitmap bitmap, int rotationDegrees);

    /**
     * Starts an empty session with the given placeholder size.
     *
     * @param mPictureSize the size, in pixels of the empty placeholder.
     */
    public void startEmpty(Size mPictureSize);

    /**
     * Starts the session by adding a placeholder to the filmstrip and adding
     * notifications.
     *
     * @param placeholder a valid encoded bitmap to be used as the placeholder.
     * @param progressMessageId the message to be used to the progress
     *            notification initially. This can later be changed using
     *            {@link #setProgressMessage(int)}.
     */
    public void startSession(byte[] placeholder, int progressMessageId);

    /**
     * Starts the session by adding a placeholder to the filmstrip and adding
     * notifications.
     *
     * @param placeholder a valid bitmap to be used as the placeholder.
     * @param progressMessageId the message to be used to the progress
     *            notification initially. This can later be changed using
     *            {@link #setProgressMessage(int)}.
     */
    public void startSession(Bitmap placeholder, int progressMessageId);

    /**
     * Starts the session by marking the item as in-progress and adding
     * notifications.
     *
     * @param uri the URI of the item to be re-processed.
     * @param progressMessageId the message to be used to the progress
     *            notification initially. This can later be changed using
     *            {@link #setProgressMessage(int)}.
     */
    public void startSession(Uri uri, int progressMessageId);

    /**
     * Cancel the session without a final result. The session will be removed
     * from the film strip, progress notifications will be cancelled.
     */
    public void cancel();

    /**
     * Finish the session by saving the image to disk. Will add the final item
     * in the film strip and remove the progress notifications.
     */
    public void saveAndFinish(byte[] data, int width, int height, int orientation,
            ExifInterface exif, OnMediaSavedListener listener);

    /**
     * Will create and return a {@link StackSaver} for saving out a number of
     * media items to a stack. The name of the stack will be the title of this
     * capture session.
     */
    public StackSaver getStackSaver();

    /**
     * Finishes the session. Resources may be held during notification of
     * finished state, {@link #finalize()} must be called to fully complete
     * the session.
     */
    public void finish();

    /**
     * Finish the session and indicate it failed. Resources may be held during
     * notification of finished state, {@link #finalize()} must be called to
     * fully complete the session.
     */
    public void finishWithFailure(int failureMessageId, boolean removeFromFilmstrip);

    /**
     * All processing complete, finalize the session and remove any resources.
     */
    public void finalize();

    /**
     * Returns the file to where the final output of this session should be
     * stored. This is only available after startSession has been called and
     * will become unavailable after finish() was called.
     */
    public TemporarySessionFile getTempOutputFile();

    /**
     * Returns the URI to the final output of this session. This is only
     * available after startSession has been called.
     */
    public Uri getUri();

    /**
     * Updates the preview from the file created from
     * {@link #getTempOutputFile()}.
     */
    public void updatePreview();

    /**
     * Adds a progress listener to this session.
     */
    public void addProgressListener(ProgressListener listener);

    /**
     * Removes the given progress listener from this session.
     */
    public void removeProgressListener(ProgressListener listener);

    /**
     * Returns the associated StatsCollector Object
     * @return
     */
    public CaptureSessionStatsCollector getCollector();
}
