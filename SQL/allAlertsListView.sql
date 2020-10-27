SELECT courseAlerts.id, courseAlerts.leadTime, courseAlerts.subsequent, 0 AS assessment, CASE courseAlerts.subsequent
	WHEN 1 THEN
		CASE WHEN courses.actualEnd IS NULL THEN courses.expectedEnd ELSE courses.actualEnd END
	ELSE
		CASE WHEN courses.actualStart IS NULL THEN courses.expectedStart ELSE courses.actualStart END
	END AS eventDate, CASE courseAlerts.subsequent
	WHEN 1 THEN
		CASE
			WHEN courses.actualEnd IS NULL THEN
				CASE WHEN courses.expectedEnd IS NULL THEN NULL ELSE courses.expectedEnd - courseAlerts.leadTime END
			ELSE
				courses.actualEnd - courseAlerts.leadTime
			END
	ELSE CASE
		WHEN courses.actualStart IS NULL THEN
			CASE WHEN courses.expectedStart IS NULL THEN NULL ELSE courses.expectedStart - courseAlerts.leadTime END
		ELSE
			courses.actualStart - courseAlerts.leadTime
		END
	END AS alertDate, courses.number AS code, courses.title, NULL as type, courses.status, courseAlerts.courseId
	FROM courseAlerts LEFT JOIN courses ON courseAlerts.courseId=courses.id
UNION SELECT assessmentAlerts.id, assessmentAlerts.leadTime, assessmentAlerts.subsequent, 1 AS assessment,
	CASE assessmentAlerts.subsequent WHEN 1 THEN assessments.completionDate ELSE assessments.goalDate END AS eventDate, CASE assessmentAlerts.subsequent
		WHEN 1 THEN
			CASE WHEN assessments.completionDate IS NULL THEN NULL ELSE assessments.completionDate - assessmentAlerts.leadTime END
		ELSE
			CASE WHEN assessments.goalDate IS NULL THEN NULL ELSE assessments.goalDate - assessmentAlerts.leadTime END
		END AS alertDate, assessments.code, CASE WHEN assessments.name IS NULL THEN '' ELSE assessments.name END as title, assessments.type, assessments.status, assessments.courseId
	FROM assessmentAlerts LEFT JOIN assessments ON assessmentAlerts.assessmentId=assessments.id