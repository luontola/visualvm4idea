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

import java.util.Map;

/**
 * @author Esko Luontola
 * @since 17.10.2008
 */
@RunWith(JDaveRunner.class)
@Group({"fast"})
public class ParameterParserSpec extends Specification<Object> {

    public class AParameterParser {

        private Map<String, String> params;

        public Object create() {
            return null;
        }

        public void parsesEmptyParameters() {
            params = ParameterParser.parse("");
            specify(params.size(), should.equal(0));
        }

        public void parsesASingleParameter() {
            params = ParameterParser.parse("foo=bar");
            specify(params.size(), should.equal(1));
            specify(params.get("foo"), should.equal("bar"));
        }

        public void parsesMultipleParameters() {
            params = ParameterParser.parse("foo=1,bar=2");
            specify(params.size(), should.equal(2));
            specify(params.get("foo"), should.equal("1"));
            specify(params.get("bar"), should.equal("2"));
        }

        public void allowsTheKeyValueSeparatorCharInAValue() {
            params = ParameterParser.parse("foo=1=2");
            specify(params.size(), should.equal(1));
            specify(params.get("foo"), should.equal("1=2"));
        }
    }
}
