/*
 * Copyright (c) 2013 Hal Hildebrand, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hellblazer.tron.documentation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;

import javax.tools.SimpleJavaFileObject;

import com.google.common.io.CharStreams;

/**
 * 
 * @author hhildebrand
 * 
 */
public final class UrlJavaFileObject extends SimpleJavaFileObject {

    private final String fileObjectContent;

    UrlJavaFileObject(URL url) throws URISyntaxException {
        super(url.toURI(), Kind.SOURCE);

        try (InputStream in = url.openStream()) {
            Reader javaSourceReader = new InputStreamReader(in);
            this.fileObjectContent = CharStreams.toString(javaSourceReader);
        } catch (IOException e) {
            throw new IllegalStateException(
                                            "IOException during reading JavaFileObject content...",
                                            e);
        }
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return fileObjectContent;
    }
}