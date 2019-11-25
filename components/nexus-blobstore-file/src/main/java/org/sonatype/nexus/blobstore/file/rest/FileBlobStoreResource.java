/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2008-present Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package org.sonatype.nexus.blobstore.file.rest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.sonatype.goodies.common.ComponentSupport;
import org.sonatype.nexus.blobstore.api.BlobStoreConfiguration;
import org.sonatype.nexus.blobstore.api.BlobStoreManager;
import org.sonatype.nexus.blobstore.file.FileBlobStore;
import org.sonatype.nexus.blobstore.rest.BlobStoreResourceUtil;
import org.sonatype.nexus.rest.Resource;
import org.sonatype.nexus.rest.WebApplicationMessageException;
import org.sonatype.nexus.validation.Validate;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.sonatype.nexus.rest.APIConstants.BETA_API_PREFIX;

/**
 * @since 3.19
 */
@Named
@Singleton
@Path(FileBlobStoreResource.RESOURCE_URI)
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class FileBlobStoreResource
    extends ComponentSupport
    implements Resource, FileBlobStoreResourceDoc
{
  public static final String RESOURCE_URI = BETA_API_PREFIX + "/blobstores/file";

  private BlobStoreManager blobStoreManager;

  @Inject
  public FileBlobStoreResource(final BlobStoreManager blobStoreManager) {
    this.blobStoreManager = checkNotNull(blobStoreManager);
  }

  @Override
  @RequiresAuthentication
  @RequiresPermissions("nexus:blobstores:create")
  @POST
  @Path("/")
  @Validate
  public void createFileBlobStore(@Valid final FileBlobStoreApiCreateRequest request) throws Exception {
    BlobStoreConfiguration configuration = request.toBlobStoreConfiguration(blobStoreManager.newConfiguration());

    blobStoreManager.create(configuration);
  }

  @Override
  @RequiresAuthentication
  @RequiresPermissions("nexus:blobstores:update")
  @PUT
  @Path("/{name}")
  @Validate
  public void updateFileBlobStore(@PathParam("name") final String name,
                                  @Valid final FileBlobStoreApiUpdateRequest request)
      throws Exception
  {
    // Confirm that the blobstore name and type are the expected name and type
    getBlobStoreConfiguration(name);

    BlobStoreConfiguration configuration = request.toBlobStoreConfiguration(blobStoreManager.newConfiguration());
    configuration.setName(name);

    blobStoreManager.update(configuration);
  }

  @Override
  @RequiresAuthentication
  @RequiresPermissions("nexus:blobstores:read")
  @GET
  @Path("/{name}")
  public FileBlobStoreApiModel getFileBlobStoreConfiguration(@PathParam("name") final String name)
  {
    BlobStoreConfiguration configuration = getBlobStoreConfiguration(name);

    return new FileBlobStoreApiModel(configuration);
  }

  private BlobStoreConfiguration getBlobStoreConfiguration(final String name) {
    if (!blobStoreManager.exists(name)) {
      BlobStoreResourceUtil.throwBlobStoreNotFoundException();
    }

    BlobStoreConfiguration configuration = blobStoreManager.get(name).getBlobStoreConfiguration();

    if (!configuration.getType().equals(FileBlobStore.TYPE)) {
      throw new WebApplicationMessageException(
          BAD_REQUEST,
          "\"Unable to read non-file blob store configuration (type was " + configuration.getType() + ")\"",
          APPLICATION_JSON
      );
    }
    return configuration;
  }
}
