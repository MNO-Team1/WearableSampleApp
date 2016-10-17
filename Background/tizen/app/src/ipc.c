#include "main.h"
#include "ipc.h"
#include "defines-app.h"
#include <message_port.h>


bool
test_check_remote_port()
{
	dlog_print(DLOG_INFO, TAG, "<<<<<<<<<<<test_check_remote_port>>>>>>>>>>>");
   int ret;
   bool found;

   ret = message_port_check_remote_port(SERVICE_APP_ID, REMOTE_MESSAGE_PORT_NAME, &found);

   if (ret != MESSAGE_PORT_ERROR_NONE)
   {
      dlog_print(DLOG_ERROR, TAG, "message_port_check_remote_port error : %d", ret);
   }
   dlog_print(DLOG_INFO, TAG, "<<<<<<<<<<<test_check_remote_port>>>>>>>>>>> %d --- %d", ret, found);

   return found;
}

void
message_receive_cb(int local_port_id, const char *remote_app_id, const char *remote_port,
                bool trusted_remote_port, bundle *message, void *user_data)
{
   char *data = NULL;
   bundle_get_str(message, "data", &data);

   dlog_print(DLOG_INFO, TAG, "APP-- Message from %s, command: %s",
              remote_app_id, data);
	update_ui(data);
}

void
init_register_local_port(void ){
	int port_id = message_port_register_local_port(LOCAL_MESSAGE_PORT_NAME, message_receive_cb, NULL);
	if (port_id < 0)
	{
	   dlog_print(DLOG_ERROR, LOG_TAG, "Port register error : %d", port_id);
	}
	else
	{
	   dlog_print(DLOG_INFO, LOG_TAG, "port_id : %d", port_id);
	}
}


void
send_message(char* data)
{
   int ret;
   bundle *b = bundle_create();
   bundle_add_str(b, "command", data);
   ret = message_port_send_message(SERVICE_APP_ID, REMOTE_MESSAGE_PORT_NAME, b);
   if (ret != MESSAGE_PORT_ERROR_NONE)
   {
      dlog_print(DLOG_ERROR, TAG, "APP-- message_port_check_remote_port error : %d", ret);
   }
   else
   {
      dlog_print(DLOG_INFO, TAG, "APP-- Send message done");
   }
   bundle_free(b);
}
