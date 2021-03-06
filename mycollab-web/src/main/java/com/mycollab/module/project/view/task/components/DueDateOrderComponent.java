/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mycollab.module.project.view.task.components;

import com.mycollab.common.i18n.GenericI18Enum;
import com.mycollab.core.utils.SortedArrayMap;
import com.mycollab.module.project.domain.SimpleTask;
import com.mycollab.vaadin.MyCollabUI;
import com.mycollab.vaadin.UserUIContext;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

/**
 * @author MyCollab Ltd
 * @since 5.1.1
 */
public class DueDateOrderComponent extends TaskGroupOrderComponent {
    private SortedArrayMap<DateTime, DefaultTaskGroupComponent> dueDateAvailables = new SortedArrayMap<>();
    private DefaultTaskGroupComponent unspecifiedTasks;

    @Override
    public void insertTasks(List<SimpleTask> tasks) {
        for (SimpleTask task : tasks) {
            if (task.getDeadline() != null) {
                Date dueDate = task.getDeadline();
                DateTime jodaTime = new DateTime(dueDate, DateTimeZone.UTC);
                DateTime monDay = jodaTime.dayOfWeek().withMinimumValue();
                if (dueDateAvailables.containsKey(monDay)) {
                    DefaultTaskGroupComponent groupComponent = dueDateAvailables.get(monDay);
                    groupComponent.insertTask(task);
                } else {
                    DateTime maxValue = monDay.dayOfWeek().withMaximumValue();
                    DateTimeFormatter formatter = DateTimeFormat.forPattern(MyCollabUI.getLongDateFormat());
                    String monDayStr = formatter.print(monDay);
                    String sundayStr = formatter.print(maxValue);
                    String titleValue = String.format("%s - %s", monDayStr, sundayStr);

                    DefaultTaskGroupComponent groupComponent = new DefaultTaskGroupComponent(titleValue);
                    dueDateAvailables.put(monDay, groupComponent);
                    int index = dueDateAvailables.getKeyIndex(monDay);
                    if (index > -1) {
                        addComponent(groupComponent, index);
                    } else {
                        addComponent(groupComponent);
                    }

                    groupComponent.insertTask(task);
                }
            } else {
                if (unspecifiedTasks == null) {
                    unspecifiedTasks = new DefaultTaskGroupComponent(UserUIContext.getMessage(GenericI18Enum.OPT_UNDEFINED));
                    addComponent(unspecifiedTasks);
                }
                unspecifiedTasks.insertTask(task);
            }
        }
    }
}
