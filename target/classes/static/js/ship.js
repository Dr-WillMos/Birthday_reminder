

// 显示Toast通知
function showToast(message, type = 'info') {
    const toastContainer = document.getElementById('toastContainer');
    
    // 如果没有 toastContainer，使用 alert 代替
    if (!toastContainer) {
        alert(message);
        return;
    }
    
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    const toastBody = document.createElement('div');
    toastBody.className = 'd-flex';

    const toastContent = document.createElement('div');
    toastContent.className = 'toast-body';
    toastContent.textContent = message;

    const closeBtn = document.createElement('button');
    closeBtn.type = 'button';
    closeBtn.className = 'btn-close btn-close-white me-2 m-auto';
    closeBtn.setAttribute('data-bs-dismiss', 'toast');
    closeBtn.setAttribute('aria-label', 'Close');

    toastBody.appendChild(toastContent);
    toastBody.appendChild(closeBtn);
    toast.appendChild(toastBody);

    toastContainer.appendChild(toast);

    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();

    // 自动移除
    setTimeout(() => {
        toast.remove();
    }, 5000);
}

// 当DOM完全加载后执行
document.addEventListener('DOMContentLoaded', function () {
    // 初始化变量
    let currentPage = 1;
    let pageSize = 10;
    let totalPages = 0;
    let searchName = '';
    let selectedTag = '';

    // 获取DOM元素
    const relationshipTableBody = document.getElementById('relationshipTableBody');
    const pagination = document.getElementById('pagination');
    const searchButton = document.getElementById('searchButton');
    const searchInput = document.getElementById('searchInput');
    const tagFilter = document.getElementById('tagFilter');
    const addRelationshipBtn = document.getElementById('addRelationshipBtn');
    const relationshipModal = new bootstrap.Modal(document.getElementById('relationshipModal'));
    const modalTitle = document.getElementById('modalTitle');
    const saveRelationshipBtn = document.getElementById('saveButton');

    // 初始化页面
    loadRelationships();

    // 事件监听器
    searchButton.addEventListener('click', function () {
        searchName = searchInput.value.trim();
        currentPage = 1;
        loadRelationships();
    });


    tagFilter.addEventListener('change', function () {
        selectedTag = this.value;
        currentPage = 1;
        loadRelationships();
    });

    addRelationshipBtn.addEventListener('click', function () {
        clearForm();
        modalTitle.textContent = '添加亲友';
        relationshipModal.show();
    });

    saveRelationshipBtn.addEventListener('click', saveRelationship);
    
    // 监听“是否替我祝贺”选项的变化
    document.getElementById('congratulateEnabled').addEventListener('change', function() {
        toggleCongratulateFields(this.value === 'true');
    });

    /**
     * 切换祝贺相关字段的显示/隐藏
     */
    function toggleCongratulateFields(show) {
        const relationshipEmailGroup = document.getElementById('relationshipEmailGroup');
        const greetingGroup = document.getElementById('greetingGroup');
        const selfCallGroup = document.getElementById('selfCallGroup');
        
        if (show) {
            relationshipEmailGroup.style.display = 'block';
            greetingGroup.style.display = 'block';
            selfCallGroup.style.display = 'block';
        } else {
            relationshipEmailGroup.style.display = 'none';
            greetingGroup.style.display = 'none';
            selfCallGroup.style.display = 'none';
        }
    }

    /**
     * 加载亲友关系列表
     */
    function loadRelationships() {
        // 显示加载中状态
        relationshipTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-5">
                    <div class="spinner-border" role="status" style="color: #667eea;">
                        <span class="visually-hidden">加载中...</span>
                    </div>
                    <p class="mt-3 text-muted">正在加载数据...</p>
                </td>
            </tr>
        `;

        API.relationship.getList(currentPage, pageSize, searchName, selectedTag)
            .then(response => {
                console.log('加载亲友关系成功:', response);

                // 清空表格
                relationshipTableBody.innerHTML = '';

                // 更新分页信息
                totalPages = response.totalPages;

                // 检查是否有数据
                if (response.content.length === 0) {
                    relationshipTableBody.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center py-5">
                                <i class="fas fa-inbox" style="font-size: 3rem; color: #cbd5e0;"></i>
                                <p class="mt-3 text-muted">暂无亲友数据，点击上方"添加亲友"开始使用吧！</p>
                            </td>
                        </tr>
                    `;
                    pagination.innerHTML = '';
                    return;
                }

                // 渲染表格数据
                response.content.forEach(relationship => {
                    const row = document.createElement('tr');

                    // 设置标签样式
                    let tagClass = '';
                    switch (relationship.tag) {
                        case '家人':
                            tagClass = 'bg-success';
                            break;
                        case '朋友':
                            tagClass = 'bg-primary';
                            break;
                        case '同事':
                            tagClass = 'bg-purple';
                            break;
                        default:
                            tagClass = 'bg-secondary';
                    }
                    
                    // 转换是否提醒和是否祝贺的显示
                    const reminderText = relationship.remindEnabled === 1 ? '是' : '否';
                    const congratulateText = relationship.congratulateEnabled === 1 ? '是' : '否';

                    // 构建行内容
                    row.innerHTML = `
                        <td>${relationship.name}</td>
                        <td>${relationship.birthday}</td>
                        <td><span class="badge ${tagClass}">${relationship.tag}</span></td>
                        <td>${reminderText}</td>
                        <td>${congratulateText}</td>
                        <td>
                            <button class="btn btn-sm btn-outline-primary edit-btn" data-id="${relationship.id}">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-sm btn-outline-danger delete-btn" data-id="${relationship.id}">
                                <i class="fas fa-trash"></i>
                            </button>
                        </td>
                    `;

                    relationshipTableBody.appendChild(row);
                });

                // 添加编辑和删除按钮的事件监听器
                document.querySelectorAll('.edit-btn').forEach(btn => {
                    btn.addEventListener('click', function () {
                        const id = this.getAttribute('data-id');
                        editRelationship(id);
                    });
                });

                document.querySelectorAll('.delete-btn').forEach(btn => {
                    btn.addEventListener('click', function () {
                        const id = this.getAttribute('data-id');
                        deleteRelationship(id);
                    });
                });

                // 更新分页控件
                renderPagination();
            })
            .catch(error => {
                console.error('加载亲友关系失败:', error);
                relationshipTableBody.innerHTML = `
                    <tr>
                        <td colspan="6" class="text-center py-5 text-danger">
                            <i class="fas fa-exclamation-triangle" style="font-size: 3rem;"></i>
                            <p class="mt-3">加载失败，请检查网络连接后重试</p>
                            <button class="btn btn-outline-primary btn-sm" onclick="location.reload()">
                                <i class="fas fa-redo me-1"></i>重新加载
                            </button>
                        </td>
                    </tr>
                `;
                showToast('加载亲友关系失败: ' + (error.message || '请检查网络连接'), 'danger');
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
            prevLi.addEventListener('click', function () {
                currentPage--;
                loadRelationships();
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
                pageLi.addEventListener('click', function () {
                    currentPage = i;
                    loadRelationships();
                });
            }

            pagination.appendChild(pageLi);
        }

        // 下一页按钮
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
        nextLi.innerHTML = '<a class="page-link" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a>';
        if (currentPage < totalPages) {
            nextLi.addEventListener('click', function () {
                currentPage++;
                loadRelationships();
            });
        }
        pagination.appendChild(nextLi);
    }

    /**
     * 编辑亲友关系
     * @param {number} id - 亲友关系ID
     */
    function editRelationship(id) {
        API.relationship.getById(id)
            .then(relationship => {
                // 填充表单
                document.getElementById('id').value = relationship.id;
                document.getElementById('name').value = relationship.name;
                document.getElementById('myEmail').value = relationship.myEmail || '';
                document.getElementById('calendarType').value = relationship.calendarType;
                document.getElementById('tag').value = relationship.tag;
                document.getElementById('birthday').value = relationship.birthday;
                // 使用正确的字段名 remindEnabled
                document.getElementById('reminderEnabled').value = relationship.remindEnabled === 1 ? 'true' : 'false';
                
                // 填充祝贺相关字段
                const congratulateEnabled = relationship.congratulateEnabled === 1;
                document.getElementById('congratulateEnabled').value = congratulateEnabled ? 'true' : 'false';
                document.getElementById('relationshipEmail').value = relationship.relationshipEmail || '';
                document.getElementById('greeting').value = relationship.greeting || '';
                document.getElementById('selfCall').value = relationship.selfCall || '';
                
                // 根据是否祝贺显示/隐藏相关字段
                toggleCongratulateFields(congratulateEnabled);

                // 设置提前提醒天数复选框
                const daysBefore = relationship.daysBefore || [];
                document.querySelectorAll('input[name="daysBefore"]').forEach(checkbox => {
                    checkbox.checked = daysBefore.includes(parseInt(checkbox.value));
                });

                document.getElementById('notes').value = relationship.notes || '';

                // 更新模态框标题
                modalTitle.textContent = '编辑亲友';

                // 显示模态框
                relationshipModal.show();
            })
            .catch(error => {
                console.error('获取亲友关系详情失败:', error);
                showToast('获取亲友关系详情失败: ' + (error.message || '请重试'), 'danger');
            });
    }


    /**
     * 删除亲友关系
     * @param {number} id - 亲友关系ID
     */
    function deleteRelationship(id) {
        if (confirm('确定要删除这个亲友关系吗？这将同时删除相关的提醒计划和记录。')) {
            API.relationship.delete(id)
                .then(() => {
                    loadRelationships();
                    showToast('删除成功', 'success');
                })
                .catch(error => {
                    console.error('删除亲友关系失败:', error);
                    showToast('删除失败: ' + (error.message || '请重试'), 'danger');
                });
        }
    }

    /**
     * 保存亲友关系（创建或更新）
     */
    function saveRelationship() {
        // 获取表单数据
        const id = document.getElementById('id').value;
        const name = document.getElementById('name').value.trim();
        const myEmail = document.getElementById('myEmail').value.trim();
        const calendarType = parseInt(document.getElementById('calendarType').value);
        const birthday = document.getElementById('birthday').value;
        const tag = document.getElementById('tag').value;
        const birthdayMonth = parseInt(birthday.split('-')[1]);
        const birthdayDay = parseInt(birthday.split('-')[2]);
        const reminderEnabled = document.getElementById('reminderEnabled').value === 'true' ? 1 : 0;
        const congratulateEnabled = document.getElementById('congratulateEnabled').value === 'true' ? 1 : 0;
        const relationshipEmail = document.getElementById('relationshipEmail').value.trim();
        const greeting = document.getElementById('greeting').value.trim();
        const selfCall = document.getElementById('selfCall').value.trim();
        const notes = document.getElementById('notes').value.trim();

        // 收集提前提醒天数
        const daysBefore = [];
        document.querySelectorAll('input[name="daysBefore"]:checked').forEach(checkbox => {
            daysBefore.push(parseInt(checkbox.value));
        });

        // 表单验证
        if (!name) {
            showToast('请输入姓名', 'warning');
            return;
        }
        
        // 如果启用祝贺，需要验证亲友邮箱
        if (congratulateEnabled === 1 && !relationshipEmail) {
            showToast('启用祝贺功能需要填写亲友邮箱', 'warning');
            return;
        }

        // 显示加载状态
        saveRelationshipBtn.disabled = true;
        saveRelationshipBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> 保存中...';

        // 构建数据对象
        const relationshipData = {
            name: name,
            myEmail: myEmail,
            calendarType: calendarType,
            tag: tag,
            birthday: birthday || null,
            birthdayMonth: birthdayMonth,
            birthdayDay: birthdayDay,
            remindEnabled: reminderEnabled,
            congratulateEnabled: congratulateEnabled,
            relationshipEmail: relationshipEmail,
            greeting: greeting,
            selfCall: selfCall,
            daysBefore: daysBefore,
            notes: notes
        };
        
        // 如果有 id，添加到数据中
        if (id) {
            relationshipData.id = parseInt(id);
        }

        // 根据是否有ID决定是创建还是更新
        const savePromise = id
            ? API.relationship.update(relationshipData)
            : API.relationship.create(relationshipData);


        savePromise
            .then(() => {
                // 关闭模态框
                relationshipModal.hide();

                // 重新加载数据
                loadRelationships();

                // 显示成功提示
                showToast('保存成功', 'success');
            })
            .catch(error => {
                console.error('保存亲友关系失败:', error);
                showToast('保存失败: ' + (error.message || '请重试'), 'danger');
            })
            .finally(() => {
                // 恢复按钮状态
                saveRelationshipBtn.disabled = false;
                saveRelationshipBtn.innerHTML = '保存';
            });
    }

    /**
     * 清空表单
     */
    function clearForm() {
        document.getElementById('id').value = '';
        document.getElementById('name').value = '';
        document.getElementById('myEmail').value = '';
        document.getElementById('calendarType').value = '0';
        document.getElementById('tag').value = '家人';
        document.getElementById('birthday').value = '';
        document.getElementById('reminderEnabled').value = 'false';
        document.getElementById('congratulateEnabled').value = 'false';
        document.getElementById('relationshipEmail').value = '';
        document.getElementById('greeting').value = '';
        document.getElementById('selfCall').value = '';
        document.querySelectorAll('input[name="daysBefore"]').forEach(checkbox => {
            checkbox.checked = false;
        });
        document.getElementById('notes').value = '';
        
        // 隐藏祝贺相关字段
        toggleCongratulateFields(false);
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

    /**
     * 格式化日期为HTML日期输入框格式 (YYYY-MM-DD)
     * @param {Date} date - 日期对象
     * @returns {string} - 格式化后的日期字符串
     */
    function formatDateForInput(date) {
        return formatDate(date);
    }


});
