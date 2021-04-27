package com.artcamera.artcamera.camera;

import androidx.collection.ArrayMap;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


public class SizeMap {

    private final ArrayMap<AspectRatio, SortedSet<Size>> mRatios = new ArrayMap<>();

    public boolean add(Size size) {
        for (AspectRatio ratio : mRatios.keySet()) {
            if (ratio.matches(size)) {
                final SortedSet<Size> sizes = mRatios.get(ratio);
                if (sizes.contains(size)) {
                    return false;
                } else {
                    sizes.add(size);
                    return true;
                }
            }
        }
        // None of the existing ratio matches the provided size; add a new key
        SortedSet<Size> sizes = new TreeSet<>();
        sizes.add(size);
        mRatios.put(AspectRatio.of(size.getWidth(), size.getHeight()), sizes);
        return true;
    }


    public void remove(AspectRatio ratio) {
        mRatios.remove(ratio);
    }

    Set<AspectRatio> ratios() {
        return mRatios.keySet();
    }

    SortedSet<Size> sizes(AspectRatio ratio) {
        if (mRatios.get(ratio) != null) {
            return mRatios.get(ratio);
        }

        AspectRatio retRatio = ratio;
        float diff = 1;
        for (AspectRatio size : ratios()) {
            if (Math.abs(ratio.toFloat() - size.toFloat()) < diff) {
                retRatio = size;
                diff = Math.abs(ratio.toFloat() - size.toFloat());
            }
        }
        return mRatios.get(retRatio);
    }

    void clear() {
        mRatios.clear();
    }

    boolean isEmpty() {
        return mRatios.isEmpty();
    }

}
