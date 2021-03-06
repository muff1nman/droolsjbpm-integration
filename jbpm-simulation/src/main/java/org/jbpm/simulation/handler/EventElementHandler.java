/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.simulation.handler;

import java.util.List;

import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.jbpm.simulation.PathContextManager;

public class EventElementHandler extends MainElementHandler {

    @Override
    public boolean handle(FlowElement element, PathContextManager manager) {
        List<EventDefinition> throwDefinitions = getEventDefinitions(element);
    
        if (throwDefinitions != null && throwDefinitions.size() > 0) {
            for (EventDefinition def : throwDefinitions) {
                String key = "";
                if (def instanceof SignalEventDefinition) {
                    key = ((SignalEventDefinition) def).getSignalRef();
                } else if (def instanceof MessageEventDefinition) {
                    key = ((MessageEventDefinition) def).getMessageRef()
                            .getId();
                } else if (def instanceof LinkEventDefinition) {
                    key = ((LinkEventDefinition) def).getName();
                } else if (def instanceof CompensateEventDefinition) {
                    key = ((CompensateEventDefinition) def)
                            .getActivityRef().getId();
                } else if (def instanceof ErrorEventDefinition) {
                    key = ((ErrorEventDefinition) def)
                            .getErrorRef().getId();
                }

                FlowElement catchEvent = manager.getCatchingEvents().get(key);
                if (catchEvent != null) {
                    super.handle(catchEvent, manager);
                } else {
                    // not supported event definition
                    manager.finalizePath();
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
