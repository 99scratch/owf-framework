package ozone.owf.util

import static ozone.owf.enums.OwfApplicationSetting.*
import grails.util.Environment
import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.hibernate.event.PostLoadEvent
import org.hibernate.event.PostUpdateEvent
import org.ozoneplatform.appconfig.server.domain.model.ApplicationConfiguration
import org.ozoneplatform.auditing.format.cef.Extension
import org.ozoneplatform.auditing.hibernate.AbstractAuditLogListener
import org.springframework.web.context.request.RequestContextHolder

import ozone.owf.grails.services.AccountService
import ozone.owf.grails.services.OwfApplicationConfigurationService
import org.springframework.beans.BeansException
class AuditLogListener extends AbstractAuditLogListener {

    GrailsApplication grailsApplication
	
    AccountService accountService

	OwfApplicationConfigurationService owfApplicationConfigurationService
	
	def jbFilter
	
	
	
	@Override
	public void onPostUpdate(PostUpdateEvent event) {
		if(!(event.getEntity() instanceof ApplicationConfiguration)){
			super.onPostUpdate(event)
		}		
	}

	@Override
	public void onPostLoad(PostLoadEvent event) {
		if(!(event.getEntity() instanceof ApplicationConfiguration)){
			super.onPostLoad(event)
		}		
	}

	@Override
    public boolean doCefLogging() {
		if(Environment.getCurrent().equals(Environment.TEST))
			return true
		if(getRequest() == null)
			return false
		if(this.getRequest().getAttribute(CEF_LOGGING_ENABLED.getCode())== null){
			this.getRequest().setAttribute(CEF_LOGGING_ENABLED.getCode(), owfApplicationConfigurationService.is(CEF_LOGGING_ENABLED))
		}
		return this.getRequest().getAttribute(CEF_LOGGING_ENABLED.getCode())
    }

	
	
    @Override
    public boolean doCefObjectAccessLogging(){
		if(Environment.getCurrent().equals(Environment.TEST))
			return true
		if(getRequest() == null)
			return false		
		if(this.getRequest().getAttribute(CEF_OBJECT_ACCESS_LOGGING_ENABLED.getCode())== null){
			this.getRequest().setAttribute(CEF_OBJECT_ACCESS_LOGGING_ENABLED.getCode(), owfApplicationConfigurationService.is(CEF_OBJECT_ACCESS_LOGGING_ENABLED))
		}
		return this.getRequest().getAttribute(CEF_OBJECT_ACCESS_LOGGING_ENABLED.getCode())
    }

    @Override
    public String getApplicationVersion() {
        return grailsApplication.metadata['app.version']
    }

    @Override
    public String getUserName() {
        return accountService.getLoggedInUsername()
    }

    @Override
    public String getHostClassification() {
		try{
			if(!jbFilter)
				jbFilter = this.grailsApplication.getMainContext().getBean("JBlocksFilter")
			return jbFilter?.configMessage
		} catch (BeansException ex){
			return Extension.UNKOWN_VALUE
		}
    }

    @Override
    public HttpServletRequest getRequest(){
        return RequestContextHolder?.getRequestAttributes()?.getRequest()
    }
}
