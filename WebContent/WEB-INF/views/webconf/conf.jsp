<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="org.json.JSONArray" %>    
<%@page import="java.util.Collection" %>
<%@page import="com.imeeting.mvc.model.conference.*" %>
<%@page import="com.imeeting.mvc.model.conference.attendee.*" %>
<%@page import="com.imeeting.web.user.UserBean" %>
<% 
	UserBean user = (UserBean) session.getAttribute(UserBean.SESSION_BEAN);
	ConferenceModel conference = (ConferenceModel)request.getAttribute("conference"); 
	Collection<AttendeeModel> attendeeCollection = conference.getAllAttendees();
%>
<!DOCTYPE html>
<html lang="zh">
  <head>
    <title>智会-会议<%=conference.getConferenceId() %></title>
	<jsp:include page="../common/_head.jsp"></jsp:include>
  </head>

  <body>
    <jsp:include page="../common/afterlogin_navibar.jsp"></jsp:include>

    <div class="container">
    	<div id="divConfTitle" class="clearfix">
    	   <div class="pull-left">
			   <h4>会议号：<%=conference.getConferenceId() %></h4>
			   <p>提示：电话拨打 0551-62379997 可以加入会议。</p>
    	   </div>
    	</div>
		<div id="divAttendeeList" class="clearfix">
			<%
				for(AttendeeModel attendee : attendeeCollection) {
						String telephoneClass = "im-icon-phone-" + attendee.getPhoneCallStatus().name();
			%>
			<div id="div<%=attendee.getPhone()%>" class="im-attendee im-attendee-conf im-attendee-name pull-left">
				<div><i class="icon-user"></i>&nbsp;<%=attendee.getNickname()%></div>
				<div><i class="<%=telephoneClass%> im-icon im-phone-icon"></i>&nbsp;<%=attendee.getPhone()%></div>
			</div>
			<%
				}
			%>
		</div>
    </div> <!-- /container -->
    <div>
        <input id="iptConfId" type="hidden" value="<%=conference.getConferenceId()%>">
        <input id="iptUserId" type="hidden" value="<%=user.getUserName()%>">
    </div>

    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/imeetings/js/lib/jquery-1.8.0.min.js"></script>
    <script src="/imeetings/js/lib/bootstrap.min.js"></script>
    <script src="http://msg.wetalking.net/socket.io/socket.io.js"></script>
    <script src="/imeetings/js/conference.js"></script>
  </body>
</html>