package com.denysov.calculator.service;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SoundService {

    private static final double CLICK_VOLUME =
            0.16;

    private static final double EQUALS_VOLUME =
            0.18;

    private final List<AudioClip> clickSounds;

    private final AudioClip equalsSound;

    private boolean enabled;

    public SoundService() {

        clickSounds =
                List.of(
                        loadClip(
                                "/sounds/click-1.wav",
                                CLICK_VOLUME
                        ),
                        loadClip(
                                "/sounds/click-1.wav",
                                CLICK_VOLUME
                        ),
                        loadClip(
                                "/sounds/click-1.wav",
                                CLICK_VOLUME
                        )
                );

        equalsSound =
                loadClip(
                        "/sounds/equals.wav",
                        EQUALS_VOLUME
                );

        enabled =
                true;
    }

    public void playClick() {

        if (!enabled) {
            return;
        }

        int index =
                ThreadLocalRandom
                        .current()
                        .nextInt(
                                clickSounds.size()
                        );

        clickSounds
                .get(index)
                .play();
    }

    public void playEquals() {

        if (!enabled) {
            return;
        }

        equalsSound.play();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(
            boolean enabled
    ) {

        this.enabled =
                enabled;
    }

    private AudioClip loadClip(
            String resourcePath,
            double volume
    ) {

        URL resource =
                getClass()
                        .getResource(
                                resourcePath
                        );

        if (resource == null) {

            throw new IllegalStateException(
                    "Sound resource not found: "
                            + resourcePath
            );
        }

        AudioClip clip =
                new AudioClip(
                        resource.toExternalForm()
                );

        clip.setVolume(
                volume
        );

        return clip;
    }
}