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
package org.sonatype.nexus.wonderland;

/**
 * Manages cache (and expiration) of authentication tickets.
 *
 * @since 2.7
 */
public interface AuthTicketCache
{
  String EXPIRE = "${wonderland.authTicketCache.expireAfter:-20s}";

  /**
   * Add token to the cache.
   */
  void add(String user, String token, String realmName);

  /**
   * Remove token from cache.
   *
   * @return True if the token existed (was added and not yet expired)
   */
  boolean remove(String user, String token, String realmName);
}
