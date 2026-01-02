

// 当DOM完全加载后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化变量
    let currentPage = 1;
    let pageSize = 10;
    let totalPages = 0;
    let selectedRelationshipId = '';
    let selectedStatus = '';
    let relationships = []; // 存储所有亲友关系

    // 获取DOM元素
    const reminderTableBody = document.getElementById('reminderTableBody');
    const pagination = document.getElementById('pagination');
    const relationshipFilter = document.getElementById('relationshipFilter');
    const statusFilter = document.getElementById('statusFilter');
    const filterBtn = document.getElementById('filterBtn');
    const addReminderBtn = document.getElementById('addReminderBtn');
    const reminderModal = new bootstrap.Modal(document.getElementById('reminderModal'));
    const reminderForm = document.getElementById('reminderForm');
    const modalTitle = document.getElementById('modalTitle');
    const saveReminderBtn = document.getElementById('saveReminder');
    const reminderTypeSelect = document.getElementById('reminderType');
    const customDateGroup = document.getElementById('customDateGroup');
    const relationshipIdSelect = document.getElementById('relationshipId');

    // 初始化页面
    loadRelationships().then(() => {
        loadReminders();
    });

    // 事件监听器
    filterBtn.addEventListener('click', function() {
        selectedRelationshipId = relationshipFilter.value;
        selectedStatus = statusFilter.value;
        currentPage = 1;
        loadReminders();
    });

    statusFilter.addEventListener('change', function() {
        selectedStatus = this.value;
        currentPage = 1;
        loadReminders();
    });

    addReminderBtn.addEventListener('click', function() {
        clearForm();
        modalTitle.textContent = '添加提醒计划';
        reminderModal.show();
    });

    saveReminderBtn.addEventListener('click', saveReminder);

    // 根据提醒类型显示/隐藏自定义日期
    reminderTypeSelect.addEventListener('change', function() {
        if (this.value === 'BIRTHDAY') {
            customDateGroup.style.display = 'none';
        } else {
            customDateGroup.style.display = 'block';
        }
    });

    /**
     * 加载所有亲友关系
     */
    async function loadRelationships() {
        try {
            // 获取所有亲友关系（这里假设一次性获取所有，实际可能需要分页）
            const response = await API.relationship.getList(1, 100);
            relationships = response.content;

            // 填充亲友筛选下拉框
            relationshipFilter.innerHTML = '<option value="">所有亲友</option>';
            relationshipIdSelect.innerHTML = '';

            relationships.forEach(relationship => {
                // 添加到筛选下拉框
                const filterOption = document.createElement('option');
                filterOption.value = relationship.id;
                filterOption.textContent = relationship.name;
                relationshipFilter.appendChild(filterOption);

                // 添加到模态框下拉框
                const modalOption = document.createElement('option');
                modalOption.value = relationship.id;
                modalOption.textContent = relationship.name;
                relationshipIdSelect.appendChild(modalOption);
            });
        } catch (error) {
            console.error('加载亲友关系失败:', error);
            alert('加载亲友关系失败，请重试');
        }
    }

    /**
     * 加载提醒计划列表
     */
    function loadReminders() {
        // 显示加载中状态
        reminderTableBody.innerHTML = '<tr><td colspan="5" class="text-center">加载中...</td></tr>';

        API.reminder.getList(currentPage, pageSize, selectedRelationshipId)
            .then(response => {
                // 清空表格
                reminderTableBody.innerHTML = '';

                // 更新分页信息
                totalPages = response.totalPages;

                // 检查是否有数据
                if (response.content.length === 0) {
                    reminderTableBody.innerHTML = '<tr><td colspan="5" class="text-center">没有找到提醒计划</td></tr>';
                    pagination.innerHTML = '';
                    return;
                }

                // 筛选状态（如果选择了状态筛选）
                let filteredReminders = response.content;
                if (selectedStatus) {
                    // 将后端 executionStatus (0=活跃, 1=未激活) 转换为前端 active
                    filteredReminders = filteredReminders.filter(reminder => {
                        const isActive = reminder.executionStatus === 0;
                        return isActive === (selectedStatus === 'ACTIVE');
                    });
                }

                if (filteredReminders.length === 0) {
                    reminderTableBody.innerHTML = '<tr><td colspan="5" class="text-center">没有找到符合条件的提醒计划</td></tr>';
                    pagination.innerHTML = '';
                    return;
                }

                // 渲染表格数据
                filteredReminders.forEach(reminder => {
                    const row = document.createElement('tr');

                    // 查找亲友名称
                    const relationship = relationships.find(r => r.id === reminder.relationshipId);
                    const relationshipName = relationship ? relationship.name : '未知亲友';

                    // 格式化提醒日期
                    let reminderDate = '未设置';
                    if (reminder.reminderDate) {
                        reminderDate = reminder.reminderDate;  // 后端已经是字符串格式
                    }

                    // 设置提醒类型显示 - 将后端 reminderType (0/1) 转换为前端显示
                    const reminderTypeText = reminder.reminderType === 0 ? '生日提醒' : '自定义提醒';

                    // 设置状态样式 - 将后端 executionStatus (0/1) 转换为前端 active
                    const isActive = reminder.executionStatus === 0;
                    const statusClass = isActive ? 'bg-success' : 'bg-secondary';
                    const statusText = isActive ? '活跃' : '未激活';

                    // 构建行内容
                    row.innerHTML = `
                        <td>${relationshipName}</td>
                        <td>${reminderTypeText}</td>
                        <td>${reminderDate}</td>
                        <td><span class="badge ${statusClass}">${statusText}</span></td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary edit-btn" data-id="${reminder.id}">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger delete-btn" data-id="${reminder.id}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    `;

                    reminderTableBody.appendChild(row);
                });

                // 添加编辑和删除按钮的事件监听器
                document.querySelectorAll('.edit-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const id = this.getAttribute('data-id');
                        editReminder(id);
                    });
                });

                document.querySelectorAll('.delete-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const id = this.getAttribute('data-id');
                        deleteReminder(id);
                    });
                });

                // 更新分页控件
                renderPagination();
            })
            .catch(error => {
                console.error('加载提醒计划失败:', error);
                reminderTableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">加载失败，请重试</td></tr>';
            });
    }

    /**
     * 渲染分页控件
     */
    function renderPagination() {
        pagination.innerHTML = '';

        if (totalPages <= 1) {
            return;
        }

        // 上一页按钮
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
        prevLi.innerHTML = '<a class="page-link" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a>';
        if (currentPage > 1) {
            prevLi.addEventListener('click', function() {
                currentPage--;
                loadReminders();
            });
        }
        pagination.appendChild(prevLi);

        // 页码按钮
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, startPage + 4);

        for (let i = startPage; i <= endPage; i++) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
            pageLi.innerHTML = `<a class="page-link" href="#">${i}</a>`;

            if (i !== currentPage) {
                pageLi.addEventListener('click', function() {
                    currentPage = i;
                    loadReminders();
                });
            }

            pagination.appendChild(pageLi);
        }

        // 下一页按钮
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
        nextLi.innerHTML = '<a class="page-link" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a>';
        if (currentPage < totalPages) {
            nextLi.addEventListener('click', function() {
                currentPage++;
                loadReminders();
            });
        }
        pagination.appendChild(nextLi);
    }

    /**
     * 编辑提醒计划
     * @param {number} id - 提醒计划ID
     */
    function editReminder(id) {
        API.reminder.getById(id)
            .then(reminder => {
                // 填充表单 - 将后端数据映射到前端字段
                document.getElementById('reminderId').value = reminder.id;
                document.getElementById('relationshipId').value = reminder.relationshipId;
                // 将后端 reminderType (0/1) 转换为前端格式 (BIRTHDAY/CUSTOM)
                document.getElementById('reminderType').value = reminder.reminderType === 0 ? 'BIRTHDAY' : 'CUSTOM';
                document.getElementById('reminderDate').value = reminder.reminderDate ? reminder.reminderDate : '';
                document.getElementById('advanceDays').value = reminder.daysBefore || 3;
                document.getElementById('message').value = '';  // 后端没有这个字段
                // 将后端 executionStatus (0/1) 转换为前端 active (true/false)
                document.getElementById('active').checked = reminder.executionStatus === 0;

                // 根据提醒类型显示/隐藏自定义日期
                if (reminder.reminderType === 0) {  // 0 表示 BIRTHDAY
                    customDateGroup.style.display = 'none';
                } else {
                    customDateGroup.style.display = 'block';
                }

                // 更新模态框标题
                modalTitle.textContent = '编辑提醒计划';

                // 显示模态框
                reminderModal.show();
            })
            .catch(error => {
                console.error('获取提醒计划详情失败:', error);
                alert('获取提醒计划详情失败，请重试');
            });
    }

    /**
     * 删除提醒计划
     * @param {number} id - 提醒计划ID
     */
    function deleteReminder(id) {
        if (confirm('确定要删除这个提醒计划吗？')) {
            API.reminder.delete(id)
                .then(() => {
                    loadReminders();
                })
                .catch(error => {
                    console.error('删除提醒计划失败:', error);
                    alert('删除提醒计划失败，请重试');
                });
        }
    }

    /**
     * 保存提醒计划（创建或更新）
     */
    function saveReminder() {
        // 获取表单数据
        const id = document.getElementById('reminderId').value;
        const relationshipId = document.getElementById('relationshipId').value;
        const reminderType = document.getElementById('reminderType').value;
        const reminderDate = document.getElementById('reminderDate').value;
        const advanceDays = parseInt(document.getElementById('advanceDays').value);
        const message = document.getElementById('message').value.trim();
        const active = document.getElementById('active').checked;

        // 表单验证
        if (!relationshipId) {
            alert('请选择亲友');
            return;
        }

        if (reminderType === 'CUSTOM' && !reminderDate) {
            alert('请选择提醒日期');
            return;
        }

        // 构建数据对象 - 映射到后端字段
        const reminderData = {
            relationshipId: parseInt(relationshipId),
            reminderType: reminderType === 'BIRTHDAY' ? 0 : 1,  // 0-提醒用户(生日); 1-发送祝贺(自定义)
            daysBefore: advanceDays,  // 后端字段是 daysBefore
            executionStatus: active ? 0 : 1  // 0-待执行(活跃); 1-已执行(未激活)
        };
        
        // 只有自定义提醒才需要设置 reminderDate
        if (reminderType === 'CUSTOM' && reminderDate) {
            reminderData.reminderDate = reminderDate;
        }

        // 根据是否有ID决定是创建还是更新
        const savePromise = id
            ? API.reminder.update(id, reminderData)
            : API.reminder.create(reminderData);

        savePromise
            .then(() => {
                // 关闭模态框
                reminderModal.hide();

                // 重新加载数据
                loadReminders();
            })
            .catch(error => {
                console.error('保存提醒计划失败:', error);
                alert('保存提醒计划失败，请重试');
            });
    }

    /**
     * 清空表单
     */
    function clearForm() {
        document.getElementById('reminderId').value = '';
        document.getElementById('relationshipId').value = relationships.length > 0 ? relationships[0].id : '';
        document.getElementById('reminderType').value = 'BIRTHDAY';
        document.getElementById('reminderDate').value = '';
        document.getElementById('advanceDays').value = 3;
        document.getElementById('message').value = '';
        document.getElementById('active').checked = true;

        // 隐藏自定义日期
        customDateGroup.style.display = 'none';
    }

    /**
     * 格式化日期为 YYYY-MM-DD 字符串
     * @param {Date} date - 日期对象
     * @returns {string} - 格式化后的日期字符串
     */
    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
});
