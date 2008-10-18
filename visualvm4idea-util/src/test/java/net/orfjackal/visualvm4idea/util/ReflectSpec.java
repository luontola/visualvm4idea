/*
 * This file is part of VisualVM for IDEA
 *
 * Copyright (c) 2008, Esko Luontola. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above copyright notice,
 *       this list of conditions and the following disclaimer in the documentation
 *       and/or other materials provided with the distribution.
 *
 *     * Neither the name of the copyright holder nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.orfjackal.visualvm4idea.util;

import jdave.Group;
import jdave.Specification;
import jdave.junit4.JDaveRunner;
import org.junit.runner.RunWith;

/**
 * @author Esko Luontola
 * @since 18.10.2008
 */
@RunWith(JDaveRunner.class)
@Group({"fast"})
public class ReflectSpec extends Specification<Object> {

    public class ReflectCan {

        private DummyA obj = new DummyA();

        public Object create() {
            return null;
        }

        public void readFieldsOfAnObject() {
            specify(Reflect.on(obj).field("af").get().value(), should.equal("AF"));
        }

        public void readFieldsOfAnObjectsSuperclass() {
            specify(Reflect.on(obj).field("bf").get().value(), should.equal("BF"));
        }

        public void callsMethodsOfAnObject() {
            specify(Reflect.on(obj).method("am").with().value(), should.equal("AM"));
        }

        public void callsMethodsOfAnObjectsSuperclass() {
            specify(Reflect.on(obj).method("bm").with().value(), should.equal("BM"));
        }

        public void callsMethodsWithParameters() {
            specify(Reflect.on(obj).method("dup", String.class).with("x").value(), should.equal("xx"));
        }

        public void callsStaticMethodsOfAClass() {
            specify(Reflect.on(DummyA.class).method("sm").with().value(), should.equal("SM"));
        }

        public void readsStaticFieldsOfAClass() {
            specify(Reflect.on(DummyA.class).field("sf").get().value(), should.equal("SF"));
        }

        public void canNestMethodCalls() {
            specify(Reflect.on(obj).method("am").with()
                    .method("length").with().value(), should.equal(2));
        }

        public void canNestFieldReads() {
            specify(Reflect.on(obj).field("af").get()
                    .method("length").with().value(), should.equal(2));
        }

        public void canCallVoidMethods() {
            specify(Reflect.on(obj).method("av").with().value(), should.equal(null));
        }

        public void canWriteFields() {
            Reflect.on(obj).field("af").set("XYZ");
            specify(Reflect.on(obj).field("af").get().value(), should.equal("XYZ"));
        }
    }


    @SuppressWarnings({"UnusedDeclaration"})
    private static class DummyA extends DummyB {

        private String af = "AF";
        private static String sf = "SF";

        private String am() {
            return "AM";
        }

        private void av() {
        }

        private String dup(String x) {
            return x + x;
        }

        private static String sm() {
            return "SM";
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private static class DummyB {

        private String bf = "BF";

        private String bm() {
            return "BM";
        }
    }
}
