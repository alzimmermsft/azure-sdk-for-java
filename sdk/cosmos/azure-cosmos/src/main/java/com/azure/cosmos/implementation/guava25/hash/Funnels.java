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

/**
 * Funnels for common types. All implementations are serializable.
 *
 * @author Dimitris Andreou
 * @since 11.0
 */
public final class Funnels {
    private Funnels() {}

    /**
     * Returns a funnel for integers.
     *
     * @since 13.0
     */
    public static Funnel<Integer> integerFunnel() {
        return IntegerFunnel.INSTANCE;
    }

    private enum IntegerFunnel implements Funnel<Integer> {
        INSTANCE;

        public void funnel(Integer from, PrimitiveSink into) {
            into.putInt(from);
        }

        @Override
        public String toString() {
            return "Funnels.integerFunnel()";
        }
    }

}
