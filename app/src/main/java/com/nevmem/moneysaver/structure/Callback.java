package com.nevmem.moneysaver.structure;

import androidx.annotation.Nullable;

public interface Callback < T > {
    void callback(@Nullable T message);
}