/*
 * Copyright 2014-2105 Samsung Electronics Co., Ltd All Rights Reserved
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

#include <tizen.h>
#include <service_app.h>
#include "sap.h"
#include "sap_h.h"
#include "dlog.h"
#include "ipc.h"
#include "defines-app.h"


/*
 * @brief Callback function for on service create
 */
static bool _on_create_cb(void *user_data)
{
	dlog_print(DLOG_INFO, TAG, "<<<<<<<<<<<service::_on_create_cb>>>>>>>>>>>");

	return true;
}

/*
 * @brief Callback function for on service terminate
 */
static void _on_terminate_cb(void *user_data)
{
	dlog_print(DLOG_INFO, TAG, "<<<<<<<<<<<service::_on_terminate_cb>>>>>>>>>>>");
}

/*
 * @brief Callback function for on service control
 */
void _on_service_control_callback(app_control_h app_control, void *user_data)
{
	dlog_print(DLOG_INFO, TAG, "<<<<<<<<<<<service::_on_app_control_callback>>>>>>>>>>>");
}


/**
 * @brief Create service instance
 * @return Application instance on success, otherwise NULL
 */
void service_create()
{
	initialize_sap();
	init_register_local_port();
}

/**
 * @brief Destroy service instance
 */
void service_destroy(void)
{

}

/**
 * @brief Run Tizen service
 * @param[in]   argc    argc paremeter received in main
 * @param[in]   argv    argv parameter received in main
 */
int service_run(int argc, char **argv)
{
	service_app_lifecycle_callback_s cbs = {
		.create = _on_create_cb,
		.terminate = _on_terminate_cb,
		.app_control = _on_service_control_callback
	};

	return service_app_main(argc, argv, &cbs, NULL);
}

/**
 * @brief Main Functions
 */
int main(int argc, char **argv)
{
	dlog_print(DLOG_INFO, TAG, "<<<<<<<<<<<MAIN>>>>>>>>>>>");
	int result = 0;
	service_create();
	result = service_run(argc, argv);
	service_destroy();

	return result;
}

