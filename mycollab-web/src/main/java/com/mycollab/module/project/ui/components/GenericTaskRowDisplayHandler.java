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
package com.mycollab.module.project.ui.components;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Img;
import com.hp.gagawa.java.elements.Span;
import com.mycollab.configuration.StorageFactory;
import com.mycollab.html.DivLessFormatter;
import com.mycollab.module.project.ProjectLinkBuilder;
import com.mycollab.module.project.ProjectTypeConstants;
import com.mycollab.module.project.domain.ProjectGenericTask;
import com.mycollab.module.project.i18n.OptionI18nEnum;
import com.mycollab.module.project.i18n.ProjectCommonI18nEnum;
import com.mycollab.module.project.ui.ProjectAssetsManager;
import com.mycollab.vaadin.UserUIContext;
import com.mycollab.vaadin.TooltipHelper;
import com.mycollab.vaadin.ui.ELabel;
import com.mycollab.vaadin.ui.UIConstants;
import com.mycollab.vaadin.web.ui.AbstractBeanPagedList;
import com.mycollab.vaadin.web.ui.DefaultBeanPagedList;
import com.mycollab.vaadin.web.ui.WebUIConstants;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import org.vaadin.viritin.layouts.MHorizontalLayout;

import static com.mycollab.vaadin.TooltipHelper.TOOLTIP_ID;

/**
 * @author MyCollab Ltd
 * @since 5.2.4
 */
public class GenericTaskRowDisplayHandler implements DefaultBeanPagedList.RowDisplayHandler<ProjectGenericTask> {

    @Override
    public Component generateRow(AbstractBeanPagedList host, ProjectGenericTask genericTask, int rowIndex) {
        MHorizontalLayout rowComp = new MHorizontalLayout().withStyleName("list-row").withFullWidth();
        rowComp.setDefaultComponentAlignment(Alignment.TOP_LEFT);

        Div issueDiv = new Div();

        issueDiv.appendText(ProjectAssetsManager.getAsset(genericTask.getType()).getHtml());

        String status = "";
        if (genericTask.isBug()) {
            status = UserUIContext.getMessage(OptionI18nEnum.BugStatus.class, genericTask.getStatus());
        } else if (genericTask.isMilestone()) {
            status = UserUIContext.getMessage(OptionI18nEnum.MilestoneStatus.class, genericTask.getStatus());
        } else if (genericTask.isRisk()) {
            status = UserUIContext.getMessage(com.mycollab.common.i18n.OptionI18nEnum.StatusI18nEnum.class,
                    genericTask.getStatus());
        } else if (genericTask.isTask()) {
            status = UserUIContext.getMessage(com.mycollab.common.i18n.OptionI18nEnum.StatusI18nEnum.class, genericTask.getStatus());
        }
        issueDiv.appendChild(new Span().appendText(status).setCSSClass(WebUIConstants.BLOCK));

        String avatarLink = StorageFactory.getAvatarPath(genericTask.getAssignUserAvatarId(), 16);
        Img img = new Img(genericTask.getAssignUserFullName(), avatarLink).setCSSClass(UIConstants.CIRCLE_BOX)
                .setTitle(genericTask.getAssignUserFullName());
        issueDiv.appendChild(img, DivLessFormatter.EMPTY_SPACE());

        A taskLink = new A().setId("tag" + TOOLTIP_ID);
        taskLink.setAttribute("onmouseover", TooltipHelper.projectHoverJsFunction(genericTask.getType(), genericTask.getTypeId() + ""));
        taskLink.setAttribute("onmouseleave", TooltipHelper.itemMouseLeaveJsFunction());
        if (ProjectTypeConstants.BUG.equals(genericTask.getType()) || ProjectTypeConstants.TASK.equals(genericTask.getType())) {
            taskLink.appendText(genericTask.getName());
            taskLink.setHref(ProjectLinkBuilder.generateProjectItemLink(genericTask.getProjectShortName(),
                    genericTask.getProjectId(), genericTask.getType(), genericTask.getExtraTypeId() + ""));
        } else {
            taskLink.appendText(genericTask.getName());
            taskLink.setHref(ProjectLinkBuilder.generateProjectItemLink(genericTask.getProjectShortName(),
                    genericTask.getProjectId(), genericTask.getType(), genericTask.getTypeId() + ""));
        }

        issueDiv.appendChild(taskLink);
        if (genericTask.isClosed()) {
            taskLink.setCSSClass("completed");
        } else if (genericTask.isOverdue()) {
            taskLink.setCSSClass("overdue");
            issueDiv.appendChild(new Span().appendText(" - " + UserUIContext.getMessage(ProjectCommonI18nEnum.OPT_DUE_IN,
                    UserUIContext.formatDuration(genericTask.getDueDate()))).setCSSClass(UIConstants.META_INFO));
        }

        rowComp.with(ELabel.html(issueDiv.write()));
        return rowComp;
    }
}
