/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sqoop.model;

import java.util.List;

/**
 * Metadata describing all required information to build a job
 * object with two connectors and a framework.
 */
public class MJobForms extends MFormList {
  public MJobForms(List<MForm> forms) {
    super(forms);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }

    if (!(other instanceof MJobForms)) {
      return false;
    }

    MJobForms mj = (MJobForms) other;
    return super.equals(mj);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public MJobForms clone(boolean cloneWithValue) {
    MJobForms copy = new MJobForms(super.clone(cloneWithValue).getForms());
    return copy;
  }
}
