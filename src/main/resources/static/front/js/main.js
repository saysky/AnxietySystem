/**
 * 提示框
 * @param text
 * @param icon
 * @param hideAfter
 */
function showMsg(text, icon, hideAfter) {
    if (heading == undefined) {
        var heading = "Tips";
    }
    $.toast({
        text: text,
        heading: heading,
        icon: icon,
        showHideTransition: 'fade',
        allowToastClose: true,
        hideAfter: hideAfter,
        stack: 1,
        position: 'top-center',
        textAlign: 'left',
        loader: true,
        loaderBg: '#ffffff'
    });
}

function showMsgAndReload(text, icon, hideAfter) {
    if (heading == undefined) {
        var heading = "Tips";
    }
    $.toast({
        text: text,
        heading: heading,
        icon: icon,
        showHideTransition: 'fade',
        allowToastClose: true,
        hideAfter: hideAfter,
        stack: 1,
        position: 'top-center',
        textAlign: 'left',
        loader: true,
        loaderBg: '#ffffff',
        afterHidden: function () {
            window.location.reload();
        }
    });
}

function showMsgAndRedirect(text, icon, hideAfter, redirectUrl) {
    if (heading == undefined) {
        var heading = "Tips";
    }
    $.toast({
        text: text,
        heading: heading,
        icon: icon,
        showHideTransition: 'fade',
        allowToastClose: true,
        hideAfter: hideAfter,
        stack: 1,
        position: 'top-center',
        textAlign: 'left',
        loader: true,
        loaderBg: '#ffffff',
        afterHidden: function () {
            window.location.href = redirectUrl;
        }
    });
}

/**
 * 文章回帖
 */
$('#post-comment-btn').click(function () {
    const content = $('#commentContent').val();
    const commentId = $('#commentId').val();
    const postId = $('#postId').val();
    if (content.length < 3) {
        showMsg('多写一点吧', "error", 1000);
        return;
    }
    $.ajax({
        type: 'POST',
        url: '/comment/post',
        async: false,
        data: {
            'postId': postId,
            'commentId': commentId,
            'commentContent': content
        },
        success: function (data) {
            if (data.code == 1) {
                showMsgAndReload(data.msg, "success", 1000);
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});

/**
 * 触发按钮
 */
$('.reply-btn').click(function () {
    const commentId = $(this).attr('data-id');
    const userDisplayName = $('#comment-' + commentId + '-user').text();
    $('#commentId').val(commentId);
    $('#commentContent').attr('placeholder', '@' + userDisplayName + ': ');
    $('#post-comment-btn').html('回帖');
    $('#comment-cancel-btn').show();
});

/**
 * 取消回帖
 */
$('#comment-cancel-btn').click(function () {
    $('#commentId').val('');
    $('#commentContent').val('');
    $('#commentContent').attr('placeholder', '发表你的看法');
    $('#post-comment-btn').html('评论');
    $('#comment-cancel-btn').hide();
});

/**
 * 评论点赞
 */
$('.comment-like').click(function () {
    const a = $(this);
    const commentId = $(this).attr('data-id');
    const item = localStorage.getItem("comment-like-" + commentId);
    if (item != null) {
        showMsg('您已经点过赞了！', "info", 1000);
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/comment/like',
        async: false,
        data: {
            'commentId': commentId
        },
        success: function (data) {
            if (data.code == 1) {
                const count = parseInt(a.find('.tt-text').text()) + 1;
                a.find('.tt-text').text(count);
                a.attr('style', 'color: #2172cda;pointer-events: none;');
                localStorage.setItem("comment-like-" + commentId, count);
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});

/**
 * 评论点踩
 */
$('.comment-dislike').click(function () {
    const a = $(this);
    const commentId = $(this).attr('data-id');
    const item = localStorage.getItem("comment-dislike-" + commentId);
    if (item != null) {
        showMsg('您已经点过踩了！', "info", 1000);
        return;
    }
    $.ajax({
        type: 'POST',
        url: '/comment/dislike',
        async: false,
        data: {
            'commentId': commentId
        },
        success: function (data) {
            if (data.code == 1) {
                const count = parseInt(a.find('.tt-text').text()) + 1;
                a.find('.tt-text').text(count);
                a.attr('style', 'color: #2172cda;pointer-events: none;');
                localStorage.setItem("comment-dislike-" + commentId, count);
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});


/**
 * 回帖点赞
 */
$('.answer-like').click(function () {
    const a = $(this);
    const answerId = $(this).attr('data-id');
    const item = localStorage.getItem("answer-like-" + answerId);
    if (item != null) {
        showMsg('您已经点过赞了！', "info", 1000);
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/answer/like',
        async: false,
        data: {
            'answerId': answerId
        },
        success: function (data) {
            if (data.code == 1) {
                const count = parseInt(a.find('.tt-text').text()) + 1;
                a.find('.tt-text').text(count);
                a.attr('style', 'color: #2172cda;pointer-events: none;');
                localStorage.setItem("answer-like-" + answerId, count);
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});



/**
 * 回帖点踩
 */
$('.answer-dislike').click(function () {
    const a = $(this);
    const answerId = $(this).attr('data-id');
    const item = localStorage.getItem("answer-dislike-" + answerId);
    if (item != null) {
        showMsg('您已经点过赞了！', "info", 1000);
        return;
    }

    $.ajax({
        type: 'POST',
        url: '/answer/dislike',
        async: false,
        data: {
            'answerId': answerId
        },
        success: function (data) {
            if (data.code == 1) {
                const count = parseInt(a.find('.tt-text').text()) + 1;
                a.find('.tt-text').text(count);
                a.attr('style', 'color: #2172cda;pointer-events: none;');
                localStorage.setItem("answer-dislike-" + answerId, count);
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});


/**
 * 触发按钮
 */
$('.reply-answer-btn').click(function () {
    const answerId = $(this).attr('data-id');
    const userDisplayName = $('#answer-' + answerId + '-user').text();
    $('#answerId').val(answerId);
    $('#content').attr('placeholder', '@' + userDisplayName + ': ');
    $('#answer-submit-btn').html('评论');
    $('#answer-cancel-btn').show();
});

/**
 * 取消回帖评论
 */
$('#answer-cancel-btn').click(function () {
    $('#answerId').val('');
    $('#content').val('');
    $('#content').attr('placeholder', '发表你的观点');
    $('#answer-submit-btn').html('回帖');
    $('#answer-cancel-btn').hide();
});


/**
 * 文章回帖
 */
$('#answer-submit-btn').click(function () {
    const content = $('#content').val();
    const answerId = $('#answerId').val();
    const questionId = $('#questionId').val();
    if (content.length < 3) {
        showMsg('多写一点吧', "error", 1000);
        return;
    }
    let url = '/answer';
    if(answerId != null && answerId != '') {
        url = '/comment/answer'
    }
    $.ajax({
        type: 'POST',
        url: url,
        async: false,
        data: {
            'questionId': questionId,
            'answerId': answerId,
            'content': content
        },
        success: function (data) {
            if (data.code == 1) {
                showMsgAndReload(data.msg, "success", 1000);
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});

/**
 * 文章点赞
 */
$('.post-like').click(function () {
    const a = $(this);
    const postId = $(this).attr('data-id');
    const item = localStorage.getItem("post-like-" + postId);
    if (item != null) {
        showMsg('您已经点过赞了！', "info", 1000);
        return;
    }
    $.ajax({
        type: 'POST',
        url: '/post/like',
        async: false,
        data: {
            'postId': postId
        },
        success: function (data) {
            if (data.code == 1) {
                const count = parseInt(a.find('.tt-text').text()) + 1;
                a.find('.tt-text').text(count);
                a.attr('style', 'color: #2172cda;pointer-events: none;');
                localStorage.setItem("post-like-" + postId, count);
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});

/**
 * 文章点赞
 */
$('.question-like').click(function () {
    const a = $(this);
    const questionId = $(this).attr('data-id');
    $.ajax({
        type: 'POST',
        url: '/question/like',
        async: false,
        data: {
            'id': questionId
        },
        success: function (data) {
            if (data.code == 1) {
                const count = parseInt(a.find('.tt-text').text()) + 1;
                a.find('.tt-text').text(count);
                a.attr('style', 'color: #2172cda;pointer-events: none;');
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});


/**
 * 关注
 */
$('.follow-btn').click(function () {
    let parent = $(this).parent('.follow-box');
    const acceptUserId = $(this).attr('data-id');
    $.ajax({
        type: 'POST',
        url: '/question/user/follow',
        async: false,
        data: {
            'acceptUserId': acceptUserId
        },
        success: function (data) {
            if (data.code == 1) {
                parent.find('.follow-btn').hide();
                parent.find('.unfollow-btn').show();
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});

/**
 * 取关
 */
$('.unfollow-btn').click(function () {
    let parent = $(this).parent('.follow-box');
    const acceptUserId = $(this).attr('data-id');
    $.ajax({
        type: 'POST',
        url: '/question/user/unfollow',
        async: false,
        data: {
            'acceptUserId': acceptUserId
        },
        success: function (data) {
            if (data.code == 1) {
                parent.find('.follow-btn').show();
                parent.find('.unfollow-btn').hide();
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});


/**
 * 收藏
 */
$('.question-mark-btn').click(function () {
    let parent = $(this).parent('.question-mark-box');
    const id = $(this).attr('data-id');
    $.ajax({
        type: 'POST',
        url: '/question/mark',
        async: false,
        data: {
            'id': id
        },
        success: function (data) {
            if (data.code == 1) {
                parent.find('.question-mark-btn').hide();
                parent.find('.question-unmark-btn').show();
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});

/**
 * 收藏
 */
$('.question-mark').click(function () {
    const a = $(this);
    const id = $(this).attr('data-id');
    $.ajax({
        type: 'POST',
        url: '/question/mark',
        async: false,
        data: {
            'id': id
        },
        success: function (data) {
            if (data.code == 1) {
                const count = parseInt(a.find('.tt-text').text()) + 1;
                a.find('.tt-text').text(count);
                a.attr('style', 'color: #2172cda;pointer-events: none;');
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});

/**
 * 取消收藏
 */
$('.question-unmark-btn').click(function () {
    let parent = $(this).parent('.question-mark-box');
    const id = $(this).attr('data-id');
    $.ajax({
        type: 'POST',
        url: '/question/unmark',
        async: false,
        data: {
            'id': id
        },
        success: function (data) {
            if (data.code == 1) {
                parent.find('.question-mark-btn').show();
                parent.find('.question-unmark-btn').hide();
            } else {
                showMsg(data.msg, "error", 1000);
            }
        }
    });
});