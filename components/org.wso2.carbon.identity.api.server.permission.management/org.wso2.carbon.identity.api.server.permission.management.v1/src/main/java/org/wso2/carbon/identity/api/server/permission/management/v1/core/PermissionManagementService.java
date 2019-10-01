/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.api.server.permission.management.v1.core;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.api.server.common.error.APIError;
import org.wso2.carbon.identity.api.server.common.error.ErrorResponse;
import org.wso2.carbon.identity.api.server.permission.management.common.Constant;
import org.wso2.carbon.identity.api.server.permission.management.common.RolePermissionManagementServiceImplDataHolder;
import org.wso2.carbon.identity.api.server.permission.management.v1.model.PermissionObject;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.user.mgt.RolePermissionException;
import org.wso2.carbon.user.mgt.RolePermissionManagementService;
import org.wso2.carbon.user.mgt.common.Permission;

import javax.ws.rs.core.Response;

/**
 * The Permission Management Service class.
 */
public class PermissionManagementService {

    private static final Log LOG = LogFactory.getLog(PermissionManagementService.class);
    /**
     * Get all permissions array.
     *
     * @return PermissionObject[] of permissions.
     */
    public PermissionObject[] getAllPermissions() {

        try {
            String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
            RolePermissionManagementService rolePermissionManagementService =
                    RolePermissionManagementServiceImplDataHolder.getRolePermissionManagementService();
            return getPermissionObjects(rolePermissionManagementService.getAllPermissions(IdentityTenantUtil
                    .getTenantId(tenantDomain)));
        } catch (RolePermissionException e) {
            throw  handleException(e);
        }
    }

    /**
     * Convert Permission Object to PermissionObject type.
     *
     * @param permissions from backend service.
     * @return PermissionObject[] of permissions.
     */
    private PermissionObject[] getPermissionObjects(Permission[] permissions) {

        PermissionObject[] outputPermissions = new PermissionObject[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            PermissionObject permission = new PermissionObject();
            permission.setDisplayName(permissions[i].getDisplayName());
            permission.setResourcePath(permissions[i].getResourcePath());
            outputPermissions[i] = permission;
        }
        return outputPermissions;
    }

    private APIError handleException(Exception e, String... data) {

        ErrorResponse errorResponse = getErrorBuilder(data)
                .build(LOG, e, buildErrorDescription(data));

        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        return new APIError(status, errorResponse);
    }


    private ErrorResponse.Builder getErrorBuilder(String... data) {

        return new ErrorResponse.Builder()
                .withCode(Constant.ErrorMessage.ERROR_CODE_ERROR_GETTING_PERMISSIONS.getCode())
                .withMessage(Constant.ErrorMessage.ERROR_CODE_ERROR_GETTING_PERMISSIONS.getMessage())
                .withDescription(buildErrorDescription(data));
    }

    private String buildErrorDescription(String... data) {

        String errorDescription;
        if (!ArrayUtils.isEmpty(data)) {
            errorDescription = String.format(Constant.ErrorMessage
                    .ERROR_CODE_ERROR_GETTING_PERMISSIONS.getDescription(), (Object) data);
        } else {
            errorDescription = Constant.ErrorMessage.ERROR_CODE_ERROR_GETTING_PERMISSIONS.getDescription();
        }
        return errorDescription;
    }
}
