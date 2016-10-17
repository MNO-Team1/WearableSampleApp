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

#ifndef __IPC_APP_H__
#define __IPC_APP_H__
#include <glib.h>
#include <pthread.h>
#include <stdbool.h>
#include <app.h>
#include <Elementary.h>
#include <system_settings.h>
#include <efl_extension.h>
#include <dlog.h>


/**
 * @brief Send message to remote port app.
 */
void send_message(char* data);
/**
 * @brief Registring local port for message port communication with application
 */
void init_register_local_port(void );


#endif /* __IPC_APP_H__ */

