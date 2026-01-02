

const API = {
    // 基础URL，如果API有特定前缀可以在这里设置
    baseUrl: '',

    /**
     * 通用请求方法
     * @param {string} url - API端点
     * @param {string} method - HTTP方法 (GET, POST, PUT, DELETE)
     * @param {object} data - 请求数据 (可选)
     * @returns {Promise} - 返回Promise对象
     */
    request: async function(url, method, data = null) {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (data && (method === 'POST' || method === 'PUT')) {
            options.body = JSON.stringify(data);
        }

        try {
            const response = await fetch(this.baseUrl + url, options);

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const result = await response.json();

            // 根据后端 ResponseEntity 结构处理
            if (result.success) {
                return result.data; // 返回具体数据
            } else {
                throw new Error(result.message || '请求失败');
            }
        } catch (error) {
            console.error('API请求错误:', error);
            throw error;
        }
    },

    // 亲友关系API
    relationship: {
        /**
         * 获取亲友关系列表
         * @param {number} page - 页码 (从1开始)
         * @param {number} size - 每页数量
         * @param {string} name - 按姓名搜索 (可选)
         * @param {string} tag - 按标签筛选 (可选)
         * @returns {Promise} - 返回Promise对象
         */
        getList: function(page = 1, size = 10, name = '', tag = '') {
            let url = `/api/relationship/page?page=${page}&size=${size}`;
            if (name) url += `&name=${encodeURIComponent(name)}`;
            if (tag) url += `&tag=${encodeURIComponent(tag)}`;
            return API.request(url, 'GET');
        },

        /**
         * 获取单个亲友关系详情
         * @param {number} id - 亲友关系ID
         * @returns {Promise} - 返回Promise对象
         */
        getById: function(id) {
            return API.request(`/api/relationship/${id}`, 'GET');
        },

        /**
         * 创建新的亲友关系
         * @param {object} relationship - 亲友关系数据
         * @returns {Promise} - 返回Promise对象
         */
        create: function(relationship) {
            return API.request('/api/relationship', 'POST', relationship);
        },

        /**
         * 更新亲友关系
         * @param {number} id - 亲友关系ID
         * @param {object} relationship - 亲友关系数据
         * @returns {Promise} - 返回Promise对象
         */
        update: function(relationship) {
            return API.request(`/api/relationship`, 'PUT', relationship);
        },

        /**
         * 删除亲友关系
         * @param {number} id - 亲友关系ID
         * @returns {Promise} - 返回Promise对象
         */
        delete: function(id) {
            return API.request(`/api/relationship/${id}`, 'DELETE');
        }
    },

    // 提醒计划API
    reminder: {
        /**
         * 获取提醒计划列表
         * @param {number} page - 页码 (从1开始)
         * @param {number} size - 每页数量
         * @param {number} relationshipId - 按亲友关系ID筛选 (可选)
         * @returns {Promise} - 返回Promise对象
         */
        getList: function(page = 1, size = 10, relationshipId = null) {
            let url = `/api/reminders?page=${page-1}&size=${size}`;
            if (relationshipId) url += `&relationshipId=${relationshipId}`;
            return API.request(url, 'GET');
        },

        /**
         * 获取单个提醒计划详情
         * @param {number} id - 提醒计划ID
         * @returns {Promise} - 返回Promise对象
         */
        getById: function(id) {
            return API.request(`/api/reminders/${id}`, 'GET');
        },

        /**
         * 创建新的提醒计划
         * @param {object} reminderData - 提醒计划数据
         * @returns {Promise} - 返回Promise对象
         */
        create: function(reminderData) {
            return API.request('/api/reminders', 'POST', reminderData);
        },

        /**
         * 更新提醒计划
         * @param {number} id - 提醒计划ID
         * @param {object} reminderData - 提醒计划数据
         * @returns {Promise} - 返回Promise对象
         */
        update: function(id, reminderData) {
            return API.request(`/api/reminders/${id}`, 'PUT', reminderData);
        },

        /**
         * 删除提醒计划
         * @param {number} id - 提醒计划ID
         * @returns {Promise} - 返回Promise对象
         */
        delete: function(id) {
            return API.request(`/api/reminders/${id}`, 'DELETE');
        }
    },

    // 提醒记录API
    reminderRecord: {
        /**
         * 获取提醒记录列表
         * @param {number} page - 页码 (从1开始)
         * @param {number} size - 每页数量
         * @param {number} relationshipId - 按亲友关系ID筛选 (可选)
         * @returns {Promise} - 返回Promise对象
         */
        getList: function(page = 1, size = 10, relationshipId = null) {
            let url = `/api/reminder-records?page=${page-1}&size=${size}`;
            if (relationshipId) url += `&relationshipId=${relationshipId}`;
            return API.request(url, 'GET');
        },

        /**
         * 获取单个提醒记录详情
         * @param {number} id - 提醒记录ID
         * @returns {Promise} - 返回Promise对象
         */
        getById: function(id) {
            return API.request(`/api/reminder-records/${id}`, 'GET');
        },

        /**
         * 标记提醒记录为已处理
         * @param {number} id - 提醒记录ID
         * @returns {Promise} - 返回Promise对象
         */
        markAsProcessed: function(id) {
            return API.request(`/api/reminder-records/${id}/process`, 'PUT');
        }
    }
};
