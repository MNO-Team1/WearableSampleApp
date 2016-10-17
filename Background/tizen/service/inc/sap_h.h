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

#ifndef __SAP_H_H__
#define __SAP_H_H__
#include <glib.h>

/**
 * @brief Initializing sap agent
 */
void initialize_sap();

/**
 * @brief Send data to connected sap agent
 */
gboolean send_data(char *message);

//gboolean find_peers(void);



#endif /* __SAP_H_H__ */
