/*
 * Copyright (C) 2008 The Guava Authors
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

/*
 * Portions Copyright (c) Microsoft Corporation
 */

package com.azure.cosmos.implementation.guava25.hash;

import java.nio.charset.Charset;

/**
 * An abstract implementation of {@link Hasher}, which only requires subtypes to implement {@link
 * #putByte}. Subtypes may provide more efficient implementations, however.
 *
 * @author Dimitris Andreou
 */
abstract class AbstractHasher implements Hasher {
    @Override
    public Hasher putString(CharSequence charSequence, Charset charset) {
        return putBytes(charSequence.toString().getBytes(charset));
    }

    @Override
    public Hasher putBytes(byte[] bytes) {
        for (byte aByte : bytes) {
            putByte(aByte);
        }
        return this;
    }

    @Override
    public Hasher putLong(long l) {
        for (int i = 0; i < 64; i += 8) {
            putByte((byte) (l >>> i));
        }
        return this;
    }

    @Override
    public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
        funnel.funnel(instance, this);
        return this;
    }
}
