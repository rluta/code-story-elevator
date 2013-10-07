
import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends Specification {
  
  "Application" should {
    
    "implement GET /call API" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/call?atFloor=0&to=UP")
        browser.pageSource must be equalTo "OK"
      }
    }
    "implement GET /userHasEntered API" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/userHasEntered")
        browser.pageSource must be equalTo "OK"
      }
    }
    "implement GET /go API" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/go?floorToGo=1")
        browser.pageSource must be equalTo "OK"
      }
    }
    "implement GET /nextCommand API" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/nextCommand")
        browser.pageSource must be matching "^(UP|DOWN|OPEN|CLOSE|NOTHING)$"
      }
    }
    "implement GET /userHasExited API" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/userHasExited")
        browser.pageSource must be equalTo "OK"
      }
    }
    "implement GET /reset API" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/reset?cause=test")
        browser.pageSource must be equalTo "OK"
      }
    }

  }
  
}