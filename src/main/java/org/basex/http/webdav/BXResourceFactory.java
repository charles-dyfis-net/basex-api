package org.basex.http.webdav;

import static org.basex.http.webdav.BXNotAuthorizedResource.*;
import static org.basex.http.webdav.BXServletRequest.*;

import javax.servlet.http.*;

import org.basex.http.*;
import org.basex.server.*;
import org.basex.util.*;

import com.bradmcevoy.common.*;
import com.bradmcevoy.http.*;

/**
 * WebDAV resource factory. Main class for generating WebDAV resources.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Rositsa Shadura
 * @author Dimitar Popov
 */
public final class BXResourceFactory implements ResourceFactory {
  /** HTTP Context. */
  private HTTPContext http;

  /**
   * Constructor.
   * @param ht http context
   */
  BXResourceFactory(final HTTPContext ht) {
    http = ht;
  }

  @Override
  public Resource getResource(final String host, final String dbpath) {
    final Auth a = HttpManager.request().getAuthorization();
    if(a != null) http.credentials(a.getUser(), a.getPassword());

    try {
      final HttpServletRequest r = getRequest();
      Path p = Path.path(dbpath);
      if(!r.getContextPath().isEmpty()) p = p.getStripFirst();
      if(!r.getServletPath().isEmpty()) p = p.getStripFirst();
      if(p.isRoot()) return new BXRoot(http);

      final String db = p.getFirst();
      return p.getLength() == 1 ?
        dbExists(db, http) ? database(db, http) : null :
        resource(db, p.getStripFirst().toString(), http);
    } catch(final LoginException ex) {
      return NOAUTH;
    } catch(final Exception ex) {
      Util.errln(ex);
    }
    return null;
  }
}
