/*
 * Copyright 2014-2015 Samsung Electronics Co., Ltd All Rights Reserved
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
 *
 */
#include "sap.h"
#include "sap_h.h"
#include "ipc.h"
#include "defines-app.h"
#include "types-app.h"
#include "logger.h"
#include <stdlib.h>
#include <message_port.h>


/**
 * @brief Receiving message from remote port app.
 */
void
message_receive_cb(int local_port_id, const char *remote_app_id, const char *remote_port,
                bool trusted_remote_port, bundle *message, void *user_data)
{
   char *command = NULL;
   bundle_get_str(message, "command", &command);

   dlog_print(DLOG_INFO, TAG, "SERVICE-- Message from %s, command: %s",
              remote_app_id, command);
   send_data(command);
}

/**
 * @brief Send message to remote port app.
 */
void
send_message(char* data)
{
    dlog_print(DLOG_INFO, TAG, "SERVICE-- send_message - %s", data);
   int ret;
   bundle *b = bundle_create();
   bundle_add_str(b, "data", data);
   ret = message_port_send_message(REMOTE_APP_ID, REMOTE_MESSAGE_PORT_NAME, b);
   if (ret != MESSAGE_PORT_ERROR_NONE)
   {
      dlog_print(DLOG_ERROR, TAG, "SERVICE-- message_port_check_remote_port error : %d", ret);
   }
   else
   {
      dlog_print(DLOG_INFO, TAG, "SERVICE-- Send message done");
   }
   bundle_free(b);
}

/**
 * @brief Registering local port for message port communication with application
 */
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




