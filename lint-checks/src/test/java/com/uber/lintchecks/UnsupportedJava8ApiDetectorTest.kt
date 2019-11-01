/*
 * Copyright (C) 2019. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.lintchecks

import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import com.uber.lintchecks.base.test.LintTestBase
import org.junit.Test

class UnsupportedJava8ApiDetectorTest : LintTestBase() {

  @Test
  fun unsupportedJava8Api_import_errors() {
    lint()
            .files(kotlinSource("""
          package foo

          import java.lang.FunctionalInterface
          import java.nio.file.Files
          import java.time
          import java.util.Objects
          import java.util.function
          import java.util.stream

          class Sample

        """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectErrorCount(6)
  }

  @Test
  fun unsupportedJava8Api_import_as_errors() {
    lint()
            .files(kotlinSource("""
          package foo

          import java.lang.FunctionalInterface as fi
          import java.nio.file.Files as f
          import java.time as t
          import java.util.Objects as o
          import java.util.function as func
          import java.util.stream as st

          class Sample

        """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectErrorCount(6)
  }

  @Test
  fun unsupportedJava8Api_import_forbidden_methods_clean() {
    lint()
            .files(kotlinSource("""
      package foo

      import java.util.Collection
      import java.lang.Iterable
      import java.util.List
      import java.util.Map

        class Sample

      """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectClean()
  }

  @Test
  fun unsupportedJava8Api_iterable_forbidden_method_errors() {
    lint()
            .files(javaSource("""
  package foo;

  import java.lang.Iterable;

    class Sample {
        void sample() {
            Iterable<String> someStrings = new Iterable<String>() {};
            someStrings.forEach((s)-> {});
            someStrings.spliterator();
        }
    }

  """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectErrorCount(2)
  }

  @Test
  fun unsupportedJava8Api_list_forbidden_method_errors() {
    lint()
            .files(javaSource("""
  package foo;

  import java.util.List;
  import java.util.ArrayList;

    class Sample {
        void sample() {
            List<String> someList = new ArrayList<>();
            someList.add("A");
            someList.add("B");

            // List
            someList.sort();
            someList.replaceAll(() -> "C");

        }
    }

  """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectErrorCount(2)
  }

  @Test
  fun unsupportedJava8Api_map_forbidden_method_errors() {
    lint()
            .files(javaSource("""
  package foo;

  import java.util.Map;
  import java.util.HashMap;

    class Sample {
        void sample() {
            Map<String, String> someMap = new HashMap<>();
            someMap.put("1", "one");
            someMap.put("2", "two");

            someMap.getOrDefault("1", "default-one");
            someMap.putIfAbsent("3", "three");
            someMap.computeIfAbsent("4", () -> "5");
            someMap.computeIfPresent("4", () -> "6");
            someMap.compute("4", () -> "7");
            someMap.merge("8", "9", () -> "ten");
            someMap.replaceAll(() -> "10");
            someMap.forEach(() -> "22");

        }
    }

  """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectErrorCount(8)
  }

  @Test
  fun unsupportedJava8Api_collection_forbidden_method_errors() {
    lint()
            .files(javaSource("""
  package foo;

  import java.util.List;

    class Sample {
        void sample() {
            List<String> someList = new ArrayList<>();
            someList.add("A");
            someList.add("B");

            // Collection
            someList.removeIf((String s) -> s.equals("A"));
            someList.stream().count();
            someList.parallelStream().count();
        }
    }

  """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectErrorCount(3)
  }

  @Test
  fun unsupportedJava8Api_lambda_in_interface_errors() {
    lint()
            .files(javaSource("""
  package foo;
  
  public interface ClearInterface {
    static void doNothing() {}
  }
  
  public interface BadInterface {
    ClearInterface badImplementation = () -> 13;
  }
  """))
            .detector(UnsupportedJava8ApiDetector())
            .issues(UnsupportedJava8ApiDetector.ISSUE)
            .run()
            .expectErrorCount(1)
  }
}
