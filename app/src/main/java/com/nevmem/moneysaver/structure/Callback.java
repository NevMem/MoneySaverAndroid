package com.nevmem.moneysaver.structure;

import android.support.annotation.Nullable;

public interface Callback < T > {
    void callback(@Nullable T message);
}